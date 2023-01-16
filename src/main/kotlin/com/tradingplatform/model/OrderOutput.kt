package com.tradingplatform.model

data class OrderOutput(val orderId: String,val quantity: Int,
                       val type: String,
                       val price: Int)