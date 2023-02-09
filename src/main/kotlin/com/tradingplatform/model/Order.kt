package com.tradingplatform.model

import java.util.PriorityQueue

data class PriceQuantityPair(val price: Int, var quantity: Int) //Utility class to make the response json pretty

data class Order(val type: String, val quantity: Int, val price: Int, val user: User, val esopType: Int) {
    var status = "unfilled"
    var filled = ArrayList<PriceQuantityPair>()
    val id: Pair<Int, Int> = Pair(BuyOrders.size + SellOrders.size + CompletedOrders.size * 2, esopType)
    val timestamp = System.currentTimeMillis()
    var filledQuantity = 0
}

val BuyOrders = PriorityQueue { order1: Order, order2: Order ->
    when {
        order1.price > order2.price -> -1
        order1.price < order2.price -> 1
        else -> {
            (order1.timestamp - order2.timestamp).toInt()
        }
    }
}

val SellOrders = PriorityQueue { order1: Order, order2: Order ->
    when {
        order1.id.second > order2.id.second -> -1
        order1.id.second < order2.id.second -> 1
        order1.price > order2.price -> 1
        order1.price < order2.price -> -1
        else -> {
            (order1.timestamp - order2.timestamp).toInt()
        }
    }
}

val CompletedOrders = HashMap<Pair<Int, Int>, Order>()
