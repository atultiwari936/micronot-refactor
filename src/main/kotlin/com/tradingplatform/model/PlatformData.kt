package com.tradingplatform.model

import java.math.BigInteger

class PlatformData {
    companion object{
        val chargeRatio: Double = 0.02
        var feesEarned: BigInteger = BigInteger("0")
        val MAX_INVENTORY_LIMIT = 10000000

        fun calculatePlatformFees(amount: Int): Int {
            return (amount * chargeRatio).toInt()
        }
    }
}