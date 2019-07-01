package com.revolut.lev

import com.revolut.lev.dao.AccountDaoMap
import com.revolut.lev.dao.LedgerDaoList
import com.revolut.lev.requests.*
import spark.Spark.*

fun main(args: Array<String>) {
    val maxThreads = 4
    threadPool(maxThreads)
    val accountDao = AccountDaoMap()
    val ledgerDao = LedgerDaoList()

    path("/account") {

        get("/:id/balance", GetBalanceRequestHandler(accountDao, ledgerDao))

        post("/create", CreateRequestHandler(accountDao, ledgerDao))

        post("/transfer", TransferRequestHandler(accountDao, ledgerDao))

        post("/deposit", DepositRequestHandler(accountDao, ledgerDao))

        post("/withdraw", WithdrawRequestHandler(accountDao, ledgerDao))

        get("/:id/history", HistoryRequestHandler(accountDao, ledgerDao))
    }

}
