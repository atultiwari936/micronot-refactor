package com.tradingplatform.model

import com.tradingplatform.validations.maxLimitForWallet

class Wallet(var free: Int, var locked: Int, var credit: Int) {
    val MAX_WALLET_LIMIT = 10000000

    fun getFreeAmount(): Int {
        return free
    }


    fun getLockedAmount(): Int {
        return locked
    }


    fun getCreditAmount(): Int {
        return credit
    }


    fun addAmountToFree(amount: Int){
        free += amount
    }

    fun removeAmountFromFree(amount: Int){
        free -= amount
    }

    fun addAmountToLocked(amount: Int){
        locked += amount
    }

    fun removeAmountFromLocked(amount: Int){
        locked -= amount
    }

    fun addAmountToCredit(amount: Int){
        credit += amount
    }

    fun removeAmountFromCredit(amount: Int){
        credit -= amount
    }


    fun isWalletAmountWithinLimit(amount: Int): Boolean {
        return (free + locked + credit + amount <= MAX_WALLET_LIMIT)
    }


}