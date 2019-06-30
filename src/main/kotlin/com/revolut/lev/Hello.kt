package com.revolut.lev

import com.revolut.lev.dao.AccountDao
import com.revolut.lev.dao.LedgerDao
import com.revolut.lev.requests.*
import spark.Spark.*

fun main(args: Array<String>) {
    val maxThreads = 4
    threadPool(maxThreads)
    val accountDao = AccountDao()
    val ledgerDao = LedgerDao()

    path("/account") {

        get("/:id/balance", GetBalanceRequestHandler(accountDao, ledgerDao))

        post("/create", CreateRequestHandler(accountDao, ledgerDao))

        post("/transfer", TransferRequestHandler(accountDao, ledgerDao))

        post("/deposit", DepositRequestHandler(accountDao, ledgerDao))

        post("/withdraw", WithdrawRequestHandler(accountDao, ledgerDao))

        get("/:id/history", HistoryRequestHandler(accountDao, ledgerDao))
    }

}
