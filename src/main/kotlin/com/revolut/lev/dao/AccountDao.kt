package com.revolut.lev.dao

interface AccountDao {
    /**
     * Check is account exist
     */
    fun isAccountExist(index: String): Boolean

    /**
     * Return amount of money
     */
    fun getAmount(index: String): Long?

    /**
     * Return total amount in store
     */
    fun getTotalAmount(): Long

    /**
     * Add amount to current account
     */
    fun deposit(index: String, amount: Long): Long

    /**
     * Withdraw money from account to the abyss
     */
    fun withdraw(index: String, amount: Long): Long

    /**
     * Transfer current amount from one account to another
     */
    fun transfer(fromIndex: String, toIndex: String, amount: Long)

    /**
     * Create new account with random uuid
     */
    fun newAccount(): String
}