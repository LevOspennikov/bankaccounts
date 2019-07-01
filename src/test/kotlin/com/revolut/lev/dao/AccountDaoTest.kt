package com.revolut.lev.dao

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import java.util.concurrent.Executors
import java.util.ArrayList
import java.util.concurrent.TimeUnit


internal class AccountDaoTest {
    var accountDao: AccountDao? = null

    @BeforeEach
    fun setUp() {
        accountDao = AccountDaoMap()
    }

    @Test
    fun isAccountNotExist() {
        val tempAcc = "1111-111-1111"
        assertFalse(accountDao!!.isAccountExist(tempAcc))
        for (i in 0..100) {
            val account = accountDao!!.newAccount()
            assertTrue(accountDao!!.isAccountExist(account))
        }
        assertFalse(accountDao!!.isAccountExist(tempAcc))
    }

    @Test
    fun newAccountSingleThread() {
        for (i in 0..1000) {
            val account = accountDao!!.newAccount()
            assertTrue(accountDao!!.isAccountExist(account))
        }
    }

    @Test
    fun getAmount() {
        for (i in 0..1000) {
            val account = accountDao!!.newAccount()
            assertEquals(0, accountDao!!.getAmount(account))
            accountDao!!.deposit(account, 100)
            assertEquals(100, accountDao!!.getAmount(account))
        }
    }

    @Test
    fun getTotalAmount() {
        assertEquals(0, accountDao!!.getTotalAmount())
        for (i in 0..1000) {
            val account = accountDao!!.newAccount()
            assertEquals(0, accountDao!!.getAmount(account))
            accountDao!!.deposit(account, 100)
            assertEquals(100L * (i + 1), accountDao!!.getTotalAmount())
        }
    }

    @Test
    fun deposit() {
        assertEquals(0, accountDao!!.getTotalAmount())
        assertEquals(0, accountDao!!.getTotalAmount())
        for (i in 0..100) {
            val account = accountDao!!.newAccount()
            assertEquals(0, accountDao!!.getAmount(account))
            accountDao!!.deposit(account, 100)
            assertEquals(100, accountDao!!.getAmount(account))
            accountDao!!.deposit(account, 100)
            assertEquals(200, accountDao!!.getAmount(account))
        }

        val fakeAcc = "111-111-111"
        assertThrows<IllegalArgumentException>("Invalid address: $fakeAcc") {
            accountDao!!.deposit(fakeAcc, 100)
        }

        val acc = accountDao!!.newAccount()
        val invalidAmount = -100L
        assertThrows<IllegalArgumentException>("Invalid amount: $invalidAmount") {
            accountDao!!.deposit(acc, invalidAmount)
        }
    }

    @Test
    fun withdraw() {
        assertEquals(0, accountDao!!.getTotalAmount())
        for (i in 0..100) {
            val account = accountDao!!.newAccount()
            assertEquals(0, accountDao!!.getAmount(account))
            accountDao!!.deposit(account, 100)
            assertEquals(100, accountDao!!.getAmount(account))
            accountDao!!.withdraw(account, 50)
            assertEquals(50, accountDao!!.getAmount(account))
            accountDao!!.withdraw(account, 50)
            assertEquals(0, accountDao!!.getAmount(account))
        }

        val fakeAcc = "111-111-111"
        assertThrows<IllegalArgumentException>("Invalid address: $fakeAcc") {
            accountDao!!.withdraw(fakeAcc, 100)
        }

        val acc = accountDao!!.newAccount()
        val invalidAmount = -100L
        assertThrows<IllegalArgumentException>("Invalid amount: $invalidAmount") {
            accountDao!!.withdraw(acc, invalidAmount)
        }
    }

    @Test
    fun transfer() {
        assertEquals(0, accountDao!!.getTotalAmount())
        var oldAccount = accountDao!!.newAccount()
        accountDao!!.deposit(oldAccount, 100)
        for (i in 0..100) {
            val account = accountDao!!.newAccount()
            assertEquals(0, accountDao!!.getAmount(account))
            accountDao!!.transfer(oldAccount, account, 100)
            assertEquals(100, accountDao!!.getAmount(account))
            assertEquals(0, accountDao!!.getAmount(oldAccount))
            oldAccount = account
        }

        assertEquals(100, accountDao!!.getTotalAmount())

        val fakeAcc = "111-111-111"
        assertThrows<IllegalArgumentException>("Invalid address: $fakeAcc") {
            accountDao!!.transfer(oldAccount, fakeAcc, 100)
        }

        assertThrows<IllegalArgumentException>("Invalid address: $fakeAcc") {
            accountDao!!.transfer(fakeAcc, oldAccount, 100)
        }

        val invalidAmount = -100L
        assertThrows<IllegalArgumentException>("Invalid amount: $invalidAmount") {
            accountDao!!.transfer(oldAccount, fakeAcc, invalidAmount)
        }
    }

    @Test
    fun transferConcurrentTest() {
        val e = Executors.newFixedThreadPool(4)
        val array = arrayListOf(accountDao!!.newAccount(), accountDao!!.newAccount(), accountDao!!.newAccount())
        array.map {
            accountDao!!.deposit(it, 100)
        }

        for (i in 0..1000) {
            e.submit {
                val arrayCopy = array.clone() as ArrayList<String>
                arrayCopy.shuffle()
                try {
                    accountDao!!.transfer(arrayCopy[0], arrayCopy[1], 1)
                } catch (unchecked: Exception) {
                }
                if (i % 500 == 0) {
                    assertEquals(300, accountDao!!.getTotalAmount())
                }
            }
        }
    }

    @Test
    fun withdrawConcurrentTest() {
        val e = Executors.newFixedThreadPool(4)
        val array = arrayListOf(accountDao!!.newAccount(), accountDao!!.newAccount(), accountDao!!.newAccount())
        array.map {
            accountDao!!.deposit(it, 100)
        }

        assertEquals(300, accountDao!!.getTotalAmount())
        for (i in 0..299) {
            e.submit {
                try {
                    accountDao!!.withdraw(array[i % 3], 1)
                } catch (exp: Exception) {
                    assertEquals(null, exp, "Should not have exception")
                }
            }

        }
        e.shutdown()
        e.awaitTermination(30, TimeUnit.SECONDS)
        assertEquals(0, accountDao!!.getTotalAmount())
    }

    @Test
    fun depositWithdrawConcurrentTest() {
        val e = Executors.newFixedThreadPool(4)
        val array = arrayListOf(accountDao!!.newAccount(), accountDao!!.newAccount(), accountDao!!.newAccount())
        array.map {
            accountDao!!.deposit(it, 1000)
        }
        assertEquals(3000, accountDao!!.getTotalAmount())
        for (i in 0..299) {
            e.submit {
                try {
                    if (i % 6 > 2) {
                        accountDao!!.deposit(array[i % 3], 1)
                    } else {
                        accountDao!!.withdraw(array[i % 3], 1)
                    }
                } catch (exp: Exception) {
                    assertEquals(null, exp, "Should not have exception")
                }
            }

        }
        e.shutdown()
        e.awaitTermination(30, TimeUnit.SECONDS)
        assertEquals(3000, accountDao!!.getTotalAmount())
    }

}