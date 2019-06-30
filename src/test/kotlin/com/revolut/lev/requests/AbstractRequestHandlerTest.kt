package com.revolut.lev.requests

import com.revolut.lev.dao.AccountDao
import com.revolut.lev.dao.LedgerDao
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import spark.Request

internal class AbstractRequestHandlerTest {
    val accountDao = AccountDao()
    val ledgerDao = LedgerDao()

    @Test
    fun handle() {
//        val model = EasyMock.createMock(Request::class.java)
//        val dep = DepositRequestHandler(accountDao, ledgerDao)
//        dep.handle(
    }
}