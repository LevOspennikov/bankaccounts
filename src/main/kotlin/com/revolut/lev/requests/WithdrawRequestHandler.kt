package com.revolut.lev.requests

import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

import com.revolut.lev.dao.AccountDao
import com.revolut.lev.dao.LedgerDao
import kotlinx.serialization.Serializable

@Serializable
data class WithdrawRequest(val account: String, val amount: Long)

class WithdrawRequestHandler(
    accountDao: AccountDao,
    ledgerDao: LedgerDao
) :
    AbstractRequestHandler<WithdrawRequest>(accountDao, ledgerDao) {

    override fun process(request: WithdrawRequest?, queryParamsMap: Map<String, String>): Answer {
        return try {
            val account = accountDao.withdraw(request!!.account, request.amount)
            ledgerDao.writeToLedger(request.account,"external", request.amount)
            Answer(200, "ok")
        } catch (err: IllegalArgumentException) {
            ledgerDao.writeToLedger(request!!.account, "external", request.amount, err)
            Answer(400, err.message!!)
        } catch (err: Exception) {
            println(err.message)
            Answer(500, "InternalError")
        }
    }

    override fun validate(request: WithdrawRequest?, queryParamsMap: Map<String, String>): String {
        if (request!!.amount <= 0) {
            return "Wrong amount"
        }
        return ""
    }

    @UnstableDefault
    @kotlinx.serialization.ImplicitReflectionSerializer
    override fun deserialize(value: String): WithdrawRequest? {
        return Json.parse(WithdrawRequest.serializer(), value)
    }
}