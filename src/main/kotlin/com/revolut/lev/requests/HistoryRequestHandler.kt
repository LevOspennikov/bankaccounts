package com.revolut.lev.requests

import com.revolut.lev.dao.AccountDao
import com.revolut.lev.dao.LedgerDao

class HistoryRequestHandler(
    accountDao: AccountDao,
    ledgerDao: LedgerDao
) :
    AbstractRequestHandler<Unit>(accountDao, ledgerDao) {

    override fun process(request: Unit?, queryParamsMap: Map<String, String>): Answer {
        return try {
            val account = queryParamsMap[":id"]!!
            val isExist = accountDao.isAccountExist(account)
            if (!isExist) {
                Answer(404, "Account $account does not exist")
            }
            val answer = ledgerDao.getAllHistoryByAccount(account)
            Answer(200, "[ " + answer.joinToString(",") + " ]")
        } catch (err: Exception) {
            println(err.message)
            Answer(500, "InternalError")
        }
    }
}