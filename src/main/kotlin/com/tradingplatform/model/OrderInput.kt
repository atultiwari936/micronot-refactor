package com.tradingplatform.model

data class OrderInput(
    var quantity: Int,
    val type: String,
    val price: Int,
    val esopType: String="NORMAL")