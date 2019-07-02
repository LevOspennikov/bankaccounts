package com.revolut.lev.dao

import java.lang.Exception

interface LedgerDao {

    /**
     * Returns all history where account equals account from or account to
     */
    fun getAllHistoryByAccount(account: String): List<LedgerRecord>

    /**
     * Write new record to ledger
     */
    fun writeToLedger(from: String, to: String, amount: Long, error: Exception? = null)

    /**
     * ledger data structure.
     */
    class LedgerRecord(val from: String, val to: String, val amount: Long, val error: Exception? = null) {
        override fun toString(): String {
            return """ {
                |   "from": "$from",
                |   "to": "$to",
                |   "amount": $amount
            |}
            """.trimIndent()
        }
    }
}