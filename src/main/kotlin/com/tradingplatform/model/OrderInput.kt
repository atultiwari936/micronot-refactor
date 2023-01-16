package com.tradingplatform.model

data class OrderInput(val quantity: Int,
    val type: String,
    val price: Int)