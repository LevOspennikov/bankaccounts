package com.revolut.lev.requests

import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

import com.revolut.lev.dao.AccountDao
import com.revolut.lev.dao.LedgerDao
import kotlinx.serialization.Serializable
import spark.QueryParamsMap

@Serializable
data class TransferRequest(val accountTo: String, val accountFrom: String, val amount: Long)

class TransferRequestHandler(
    accountDao: AccountDao,
    ledgerDao: LedgerDao
) :
    AbstractRequestHandler<TransferRequest>(accountDao, ledgerDao) {

    override fun process(request: TransferRequest?, queryParamsMap: Map<String, String>): Answer {
        return try {
            val account = accountDao.transfer(request!!.accountTo, request.accountFrom, request.amount)
            ledgerDao.writeToLedger(request.accountTo, request.accountFrom, request.amount)
            Answer(200, "ok")
        } catch (err: IllegalArgumentException) {
            ledgerDao.writeToLedger(request!!.accountTo, request.accountFrom, request.amount, err)
            Answer(400, err.message!!)
        } catch (err: Exception) {
            println(err.message)
            Answer(500, "InternalError")
        }
    }

    override fun validate(request: TransferRequest?, queryParamsMap: Map<String, String>): String {
        if (request!!.amount <= 0) {
            return "Wrong amount"
        }
        return ""
    }

    @UnstableDefault
    @kotlinx.serialization.ImplicitReflectionSerializer
    override fun deserialize(value: String): TransferRequest? {
        return Json.parse(TransferRequest.serializer(), value)
    }
}