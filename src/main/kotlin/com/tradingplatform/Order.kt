package com.tradingplatform

data class Order constructor(val type : String, val qty: Int, val price : Int) {
    var status = "unfilled"
    var filled = ArrayList<Pair<Int,Int>>()
    val id = BuyOrders.size + SellOrders.size + CompletedOrders.size
    // The match orders function has to be called here
}


val BuyOrders = HashMap<Int,Order>()
val SellOrders = HashMap<Int,Order>()
val CompletedOrders = HashMap<Int,Order>()