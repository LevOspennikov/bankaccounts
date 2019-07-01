package com.revolut.lev.requests

import com.revolut.lev.dao.AccountDao
import com.revolut.lev.dao.AccountDaoMap
import com.revolut.lev.dao.LedgerDao
import com.revolut.lev.dao.LedgerDaoList
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import spark.Request

internal class AbstractRequestHandlerTest {
    val accountDao = AccountDaoMap()
    val ledgerDao = LedgerDaoList()

    @Test
    fun handle() {
//        val model = EasyMock.createMock(Request::class.java)
//        val dep = DepositRequestHandler(accountDao, ledgerDao)
//        dep.handle(
    }
}