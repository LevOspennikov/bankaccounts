package com.revolut.lev.dao

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.lang.Exception
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

internal class LedgerDaoTest {

    var ledgerDao: LedgerDao? = null

    @BeforeEach
    fun setUp() {
        ledgerDao = LedgerDao()
    }

    @Test
    fun writeToLedger() {
        for (i in 1..10L) {
            ledgerDao!!.writeToLedger("from", "to", i)
            ledgerDao!!.writeToLedger("from1", "to", i)
            ledgerDao!!.writeToLedger("from", "to", i, Exception("generic"))
        }
        val history = ArrayList<LedgerDao.LedgerRecord>(ledgerDao!!.getAllHistoryByAccount("from"))
        assertEquals(10, history.size)
        history.sortBy { record1 ->  record1.amount }
        for (i in 1..10L) {
            assertEquals(i, history[i.toInt() - 1].amount)
            assertEquals("from", history[i.toInt() - 1].from)
            assertNull(history[i.toInt() - 1].error)
        }
    }

    @Test
    fun writeToLedgerMultithreaded() {
        val e = Executors.newFixedThreadPool(4)
        for (i in 1..300L) {
            e.submit {
                ledgerDao!!.writeToLedger("from", "to", i)
                ledgerDao!!.writeToLedger("from1", "to", i)
                ledgerDao!!.writeToLedger("from", "to", i, Exception("generic"))
            }
        }
        e.shutdown()
        e.awaitTermination(30, TimeUnit.SECONDS)
        val history = ArrayList<LedgerDao.LedgerRecord>(ledgerDao!!.getAllHistoryByAccount("from"))
        assertEquals(300, history.size)
        history.sortBy { record1 ->  record1.amount }
        for (i in 1..300L) {
            assertEquals(i, history[i.toInt() - 1].amount)
            assertEquals("from", history[i.toInt() - 1].from)
            assertNull(history[i.toInt() - 1].error)
        }
    }


}