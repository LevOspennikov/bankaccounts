package com.revolut.lev.dao

import java.lang.Exception
import java.util.concurrent.ConcurrentLinkedQueue

class LedgerDao {
    /**
     * An array of accounts by index.
     */
    private var ledger: ConcurrentLinkedQueue<LedgerRecord> = ConcurrentLinkedQueue<LedgerRecord>()

    /**
     * Returns all history where account equals account from or account to
     */
    fun getAllHistoryByAccount(account: String): List<LedgerRecord> {
        return ledger.filter { ledgerRecord ->
            ledgerRecord.error == null
                    && (ledgerRecord.from == account || ledgerRecord.to == account)
        }
    }

    /**
     * Write new record to ledger
     */
    fun writeToLedger(from: String, to: String, amount: Long, error: Exception? = null) {
        ledger.add(LedgerRecord(from, to, amount, error))
    }

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