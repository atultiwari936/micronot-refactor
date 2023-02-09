package com.tradingplatform.model

import com.tradingplatform.data.OrderRepository

data class PriceQuantityPair(val price: Int, var quantity: Int) //Utility class to make the response json pretty

data class Order(val type: String, val quantity: Int, val price: Int, val user: User, val esopType: Int = 0) {
    var status = "unfilled"
    var filled = ArrayList<PriceQuantityPair>()
    val id: Pair<Int, Int> = Pair(
        OrderRepository.getBuyOrders().size + OrderRepository.getSellOrders().size + OrderRepository
            .getCompletedOrders().size * 2, esopType
    )
    val timestamp = System.currentTimeMillis()
    var filledQuantity = 0
}
