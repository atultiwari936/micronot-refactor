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
            for(potentialSellOrder in SellOrders){
                if(potentialSellOrder.price > price) break
                else{
                    val potentialSellOrderQty = min(qty, potentialSellOrder.qty)

                    //Update new incoming potentialSellOrder
                    filled.add(arrayOf(Pair("price",potentialSellOrder.price),Pair("quantity",potentialSellOrderQty)))
                    filledQty += potentialSellOrderQty
                    if(filledQty == qty) {
                        status = "filled"
                        CompletedOrders[id] = this
                    }
                    else{
                        if(filledQty < qty && filledQty > 0) status = "partially filled"
                        BuyOrders.add(this)
                    }

                    //Update the potentialSellOrder that matched with this
                    potentialSellOrder.filled.add(arrayOf(Pair("price",potentialSellOrder.price),Pair("quantity",potentialSellOrderQty)))
                    potentialSellOrder.filledQty += potentialSellOrderQty
                    if(potentialSellOrder.filledQty == potentialSellOrder.qty) {
                        potentialSellOrder.status = "filled"
                        SellOrders.remove(potentialSellOrder)
                        CompletedOrders[potentialSellOrder.id] = potentialSellOrder
                    }
                    if(potentialSellOrder.filledQty < potentialSellOrder.qty && potentialSellOrder.filledQty > 0) status = "partially filled"
                }
            }
        }
        else if(type == "SELL"){
            for(potentialBuyOrder in BuyOrders){
                if(potentialBuyOrder.price < price) break
                else{
                    val orderQty = min(qty, potentialBuyOrder.qty)

                    //Update new incoming order
                    filled.add(arrayOf(Pair("price",price),Pair("quantity",orderQty)))
                    filledQty += orderQty
                    if(filledQty == qty) {
                        status = "filled"
                        CompletedOrders[id] = this
                    }
                    else{
                        if(filledQty < qty && filledQty > 0) status = "partially filled"
                        SellOrders.add(this)
                    }

                    //Update the order that matched with this
                    potentialBuyOrder.filled.add(arrayOf(Pair("price",price),Pair("quantity",orderQty)))
                    potentialBuyOrder.filledQty += orderQty
                    if(potentialBuyOrder.filledQty == potentialBuyOrder.qty) {
                        potentialBuyOrder.status = "filled"
                        BuyOrders.remove(potentialBuyOrder)
                        CompletedOrders[potentialBuyOrder.id] = potentialBuyOrder
                    }
                    if(potentialBuyOrder.filledQty < potentialBuyOrder.qty && potentialBuyOrder.filledQty > 0) status = "partially filled"
                }
            }
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

