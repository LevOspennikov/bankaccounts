package com.revolut.lev.requests

import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

import com.revolut.lev.dao.AccountDao
import com.revolut.lev.dao.LedgerDao
import kotlinx.serialization.Serializable

@Serializable
data class DepositRequest(val account: String, val amount: Long)

class DepositRequestHandler(
    accountDao: AccountDao,
    ledgerDao: LedgerDao
) :
    AbstractRequestHandler<DepositRequest>(accountDao, ledgerDao) {

    override fun process(request: DepositRequest?, queryParamsMap: Map<String, String>): Answer {
        return try {
            val account = accountDao.deposit(request!!.account, request.amount)
            ledgerDao.writeToLedger("external", request.account, request.amount)
            Answer(200, "ok")
        } catch (err: IllegalArgumentException) {
            ledgerDao.writeToLedger("external", request!!.account, request.amount, err)
            Answer(400, err.message!!)
        } catch (err: Exception) {
            println(err.message)
            Answer(500, "InternalError")
        }
    }

    override fun validate(request: DepositRequest?, queryParamsMap: Map<String, String>): String {
        if (request!!.amount <= 0) {
            return "Wrong amount"
        }
        return ""
    }

    @UnstableDefault
    @kotlinx.serialization.ImplicitReflectionSerializer
    override fun deserialize(value: String): DepositRequest? {
        return Json.parse(DepositRequest.serializer(), value)
    }
}