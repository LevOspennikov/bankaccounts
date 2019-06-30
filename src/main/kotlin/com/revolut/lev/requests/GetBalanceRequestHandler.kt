package com.revolut.lev.requests

import com.revolut.lev.dao.AccountDao
import com.revolut.lev.dao.LedgerDao
import kotlinx.serialization.Serializable
import spark.QueryParamsMap

class GetBalanceRequestHandler(
    accountDao: AccountDao,
    ledgerDao: LedgerDao
) :
    AbstractRequestHandler<Unit>(accountDao, ledgerDao) {

    override fun process(request: Unit?, queryParamsMap: Map<String, String>): Answer {
        val amount: Long? = accountDao.getAmount(queryParamsMap.get(":id")!!)
        return if (amount != null) {
            Answer(200, amount.toString())
        } else {
            Answer(404,  "Account not found")
        }
    }
}