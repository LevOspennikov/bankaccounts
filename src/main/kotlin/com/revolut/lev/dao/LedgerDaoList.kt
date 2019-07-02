package com.revolut.lev.dao

import java.lang.Exception
import java.util.concurrent.ConcurrentLinkedQueue

class LedgerDaoList: LedgerDao {
    /**
     * An array of accounts by index.
     */
    private var ledger: ConcurrentLinkedQueue<LedgerDao.LedgerRecord> = ConcurrentLinkedQueue<LedgerDao.LedgerRecord>()

    /**
     * Returns all history where account equals account from or account to
     */
    override fun getAllHistoryByAccount(account: String): List<LedgerDao.LedgerRecord> {
        return ledger.filter { ledgerRecord ->
            ledgerRecord.error == null
                    && (ledgerRecord.from == account || ledgerRecord.to == account)
        }
    }

    /**
     * Write new record to ledger
     */
    override fun writeToLedger(from: String, to: String, amount: Long, error: Exception?) {
        ledger.add(LedgerDao.LedgerRecord(from, to, amount, error))
    }
}