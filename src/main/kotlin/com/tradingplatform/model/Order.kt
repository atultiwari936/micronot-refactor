package com.tradingplatform.model

import com.tradingplatform.data.OrderRepository

data class PriceQuantityPair(val price: Int, var quantity: Int) //Utility class to make the response json pretty

data class Order(
    val type: String,
    val quantity: Int,
    val price: Int,
    val user: User,
    val esopType: String = ESOPType.PERFORMANCE.value
) {
    var status = "unfilled"
    var filled = ArrayList<PriceQuantityPair>()
    val id: Int =
        OrderRepository.getBuyOrders().size + OrderRepository.getSellOrders().size + OrderRepository.getCompletedOrders().size * 2

    val timestamp = System.currentTimeMillis()
    var filledQuantity = 0
}
