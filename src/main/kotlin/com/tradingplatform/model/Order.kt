package com.tradingplatform.model
import kotlin.math.min
import java.util.PriorityQueue
data class Order constructor(val type : String, val qty: Int, val price : Int) {
    var status = "unfilled"
    var filled = ArrayList<Array<Pair<String, Int>>>()
    val id = BuyOrders.size + SellOrders.size + CompletedOrders.size
    val timestamp = System.currentTimeMillis()
    var filledQty = 0
    // The match orders function has to be called here
    init {
        if(type == "BUY"){
            for(order in SellOrders){
                if(order.price > price) break
                else{
                    val orderQty = min(qty, order.qty)

                    //Update new incoming order
                    filled.add(arrayOf(Pair("price",order.price),Pair("quantity",orderQty)))
                    filledQty += orderQty
                    if(filledQty == qty) {
                        status = "filled"
                        CompletedOrders[id] = this
                    }
                    else{
                        if(filledQty < qty && filledQty > 0) status = "partially filled"
                        BuyOrders.add(this)
                    }

                    //Update the order that matched with this
                    order.filled.add(arrayOf(Pair("price",order.price),Pair("quantity",orderQty)))
                    order.filledQty += orderQty
                    if(order.filledQty == order.qty) {
                        order.status = "filled"
                        SellOrders.remove(order)
                        CompletedOrders[order.id] = order
                    }
                    if(order.filledQty < order.qty && order.filledQty > 0) status = "partially filled"
                }
            }
        }
        else if(type == "SELL"){
            SellOrders.add(this)
        }
    }
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

