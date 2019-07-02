package com.revolut.lev.requests

import com.revolut.lev.dao.AccountDao
import com.revolut.lev.dao.LedgerDao


class CreateRequestHandler(
    accountDao: AccountDao,
    ledgerDao: LedgerDao
) :
    AbstractRequestHandler<Unit>(accountDao, ledgerDao) {

    override fun process(request: Unit?, queryParamsMap: Map<String, String>): Answer {
        val account = accountDao.newAccount()
        return Answer(200, account)
    }
}