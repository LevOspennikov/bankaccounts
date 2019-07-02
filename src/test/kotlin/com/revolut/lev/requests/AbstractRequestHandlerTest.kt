package com.revolut.lev.requests

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.revolut.lev.dao.AccountDao
import com.revolut.lev.dao.AccountDaoMap
import com.revolut.lev.dao.LedgerDao
import com.revolut.lev.dao.LedgerDaoList
import org.junit.jupiter.api.Test

import spark.Request
import spark.Response

internal class AbstractRequestHandlerTest {
    val accountDao = AccountDaoMap()
    val ledgerDao = LedgerDaoList()

    @Test
    fun handleCorrectRequest() {
        val account = accountDao.newAccount()
        val request = mock<Request> {
            on { body() } doReturn "{ \"amount\":100,\n" +
                    "\"account\":\"$account\"\n" +
                    "}"
        }
        whenever(request.params()).thenReturn(hashMapOf<String, String>())
        val response = mock<Response>()

        val dep = DepositRequestHandler(accountDao, ledgerDao)
        dep.handle(request, response)
        verify(response).status(200)
    }

    @Test
    fun handleIncorrectRequest() {
        val request = mock<Request> {
            on { body() } doReturn "{ \"amount\":100,\n" +
                    "\"account\":\"1111-1111\n" +
                    "}"
        }
        whenever(request.params()).thenReturn(hashMapOf<String, String>())
        val response = mock<Response>()

        val dep = DepositRequestHandler(accountDao, ledgerDao)
        dep.handle(request, response)
        verify(response).status(400)
    }
}
