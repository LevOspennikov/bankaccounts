package com.revolut.lev.requests

import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

import com.revolut.lev.dao.AccountDao
import com.revolut.lev.dao.LedgerDao
import kotlinx.serialization.Serializable
import spark.QueryParamsMap
import java.time.Instant
import java.time.format.DateTimeParseException
import java.util.*

class HistoryRequestHandler(
    accountDao: AccountDao,
    ledgerDao: LedgerDao
) :
    AbstractRequestHandler<Unit>(accountDao, ledgerDao) {

    override fun process(request: Unit?, queryParamsMap: Map<String, String>): Answer {
        return try {
            val account = queryParamsMap.get(":id")!!
            println(account)
            println(queryParamsMap.toMap())
            val isExist = accountDao.isAccountExist(account)
            if (!isExist) {
                Answer(404, "Account ${account} does not exist")
            }
            val answer = ledgerDao.getAllHistoryByAccount(account)
            Answer(200, "[ " + answer.joinToString(",") + " ]")
        } catch (err: Exception) {
            println(err.message)
            Answer(500, "InternalError")
        }
    }


}