package com.tradingplatform.model

import java.util.PriorityQueue

data class Order constructor(val type : String, val qty: Int, val price : Int) {
    var status = "unfilled"
    var filled = ArrayList<Pair<Int,Int>>()
    val id = BuyOrders.size + SellOrders.size + CompletedOrders.size
    val timestamp = System.currentTimeMillis()
    // The match orders function has to be called here
    init {
        executeOrders()
    }
}

fun executeOrders(){

}

val BuyOrders = PriorityQueue<Order>{order1 : Order, order2 : Order ->
    when{
        order1.price > order2.price -> -1
        order1.price < order2.price -> 1
        else -> {(order1.timestamp - order2.timestamp).toInt()}
    }
}
val SellOrders = PriorityQueue<Order>{order1 : Order, order2 : Order ->
    when{
        order1.price > order2.price -> 1
        order1.price < order2.price -> -1
        else -> {(order1.timestamp - order2.timestamp).toInt()}
    }}

val CompletedOrders = HashMap<Int, Order>()
