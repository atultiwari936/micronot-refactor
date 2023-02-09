package com.tradingplatform.model

class Wallet(private var free: Int, private var locked: Int, var credit: Int) {
    companion object {
        const val MAX_WALLET_LIMIT = 10000000
    }

    fun getFreeAmount(): Int {
        return free
    }


    fun getLockedAmount(): Int {
        return locked
    }


    fun addAmountToFree(amount: Int) {
        free += amount
    }

    fun addCredits(amount: Int){
        credit += amount
    }

    fun removeAmountFromFree(amount: Int) {
        free -= amount
    }

    fun addAmountToLocked(amount: Int) {
        locked += amount
    }

    fun removeAmountFromLocked(amount: Int) {
        locked -= amount
    }

    fun removeAmountFromCredit(amount: Int) {
        credit -= amount
    }


    fun isWalletAmountWithinLimit(amount: Int): Boolean {
        return (free + locked + credit + amount <= MAX_WALLET_LIMIT)
    }

    fun transferAmountFromFreeToLocked(amount: Int) {
        removeAmountFromFree(amount)
        addAmountToLocked(amount)
    }

}