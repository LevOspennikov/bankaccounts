package com.revolut.lev.requests

import com.revolut.lev.dao.AccountDao
import com.revolut.lev.dao.AccountDaoMap
import com.revolut.lev.dao.LedgerDao
import com.revolut.lev.dao.LedgerDaoList
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.UnstableDefault
import org.junit.Test

import org.junit.Assert.*
import org.junit.jupiter.api.assertThrows

class WithdrawRequestHandlerTest {
    val accountDao = AccountDaoMap()
    val ledgerDao = LedgerDaoList()
    val accountId = accountDao.newAccount()

    @Test
    fun process() {
        accountDao.deposit(accountId, 200)
        val dep = WithdrawRequestHandler(accountDao, ledgerDao)
        var req = WithdrawRequest(accountId, 100)
        var ans = dep.process(req, HashMap<String, String>())
        assertEquals(200, ans.httpCode)
        assertEquals((100).toLong(), accountDao.getAmount(accountId))
        assertEquals((100).toLong(), accountDao.getTotalAmount())
        assertEquals(1, ledgerDao.getAllHistoryByAccount(accountId).size)
        req = WithdrawRequest("11-111-11", 100)
        ans = dep.process(req, HashMap<String, String>())
        assertEquals(400, ans.httpCode)
        assertNotNull(ans.response)
        assertNull(accountDao.getAmount("11-111-11"))
        assertEquals(100, accountDao.getTotalAmount())
    }

    @UnstableDefault
    @Test
    @ImplicitReflectionSerializer
    fun deserialize() {
        val dep = WithdrawRequestHandler(accountDao, ledgerDao)
        val result = dep.deserialize("{ \"amount\":100,\n" +
                "\"account\":\"a467d327-a338-4d57-a35a-c3cf44f1ba8a\"\n" +
                "}")
        assertEquals(100, result!!.amount)
        assertEquals("a467d327-a338-4d57-a35a-c3cf44f1ba8a", result.account)
        assertThrows<MissingFieldException>("Field 'account' is required, but it was missing") { dep.deserialize("{ \"amount\":100\n" + "}") }
    }
}