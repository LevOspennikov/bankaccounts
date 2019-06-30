package com.revolut.lev.dao

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap

class AccountDao {
    /**
     * An map of accounts by index.
     */
    private var accounts: ConcurrentHashMap<String, Account> = ConcurrentHashMap()

    private val MAX_AMOUNT = Long.MAX_VALUE

    private fun getNumberOfAccounts(): Int {
        return accounts.size
    }

    fun isAccountExist(index: String): Boolean {
        return accounts[index] != null
    }

    private fun checkAccount(index: String) {
        if (!isAccountExist(index)) {
            throw IllegalArgumentException("Invalid address: $index")
        }
    }

    /**
     * Return amount of money
     */
    fun getAmount(index: String): Long? {
        var amount: Long? = null
        if (isAccountExist(index)) {
            println(index)
            accounts[index]!!.lock.lock()
            amount = accounts[index]!!.amount
            accounts[index]!!.lock.unlock()
        }
        return amount
    }

    /**
     * Return total amount in store
     */
    fun getTotalAmount(): Long {
        var sum: Long = 0
        for (accountEntry in accounts) {
            accountEntry.value.lock.lock()
        }
        for (account in accounts) {
            sum += account.value.amount
        }
        for (accountEntry in accounts) {
            accountEntry.value.lock.unlock()
        }
        return sum
    }

    /**
     * Add amount to current account
     */
    fun deposit(index: String, amount: Long): Long {
        if (amount <= 0)
            throw IllegalArgumentException("Invalid amount: $amount")
        checkAccount(index)

        val account: Account?
        try {
            accounts[index]!!.lock.lock()
            account = accounts[index]!!
            if (amount > MAX_AMOUNT || account.amount + amount > MAX_AMOUNT)
                throw IllegalStateException("Overflow")
            account.amount += amount
            return account.amount
        } finally {
            accounts[index]!!.lock.unlock()
        }
    }

    /**
     * Withdraw money from account to the abyss
     */
    fun withdraw(index: String, amount: Long): Long {
        if (amount <= 0)
            throw IllegalArgumentException("Invalid amount: $amount")
        checkAccount(index)
        val account: Account
        try {
            accounts[index]!!.lock.lock()
            account = accounts[index]!!
            if (account.amount - amount < 0)
                throw IllegalArgumentException("Underflow")
            account.amount -= amount
            return account.amount
        } finally {
            accounts[index]!!.lock.unlock()
        }
    }

    /**
     * Transfer current amount from one account to another
     */
    fun transfer(fromIndex: String, toIndex: String, amount: Long) {
        if (amount <= 0)
            throw IllegalArgumentException("Invalid amount: $amount")
        if (fromIndex == toIndex)
            throw IllegalArgumentException("fromIndex == toIndex")
        checkAccount(fromIndex)
        checkAccount(toIndex)
        try {
            if (fromIndex < toIndex) {
                accounts[fromIndex]!!.lock.lock()
                accounts[toIndex]!!.lock.lock()
            } else {
                accounts[toIndex]!!.lock.lock()
                accounts[fromIndex]!!.lock.lock()
            }
            val from = accounts[fromIndex]
            val to = accounts[toIndex]
            if (amount > from!!.amount)
                throw IllegalArgumentException("Underflow")
            else if (amount > MAX_AMOUNT || to!!.amount + amount > MAX_AMOUNT)
                throw IllegalStateException("Overflow")
            from.amount -= amount
            to.amount += amount
        } finally {
            accounts[toIndex]!!.lock.unlock()
            accounts[fromIndex]!!.lock.unlock()
        }
    }

    fun newAccount(): String {
        val uuid = UUID.randomUUID().toString()
        accounts[uuid] = Account()
        return uuid
    }

    /**
     * Private account data structure.
     */
    private class Account {
        /**
         * Amount of funds in this account.
         */
        internal var amount: Long = 0
        /**
         * Lock for multi-threading implementation.
         */
        internal val lock: Lock = ReentrantLock()
    }
}
