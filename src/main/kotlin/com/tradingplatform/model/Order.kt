package com.tradingplatform.model
import kotlin.math.min
import java.util.PriorityQueue

data class PriceQtyPair(val price: Int, val quantity: Int) //Utility class to make the response json pretty

data class Order constructor(val type : String, val qty: Int, val price : Int, val createdBy : String) {
    var status = "unfilled"
    var filled = ArrayList<PriceQtyPair>()
    val id = BuyOrders.size + SellOrders.size + CompletedOrders.size*2
    val timestamp = System.currentTimeMillis()
    var filledQty = 0
    // The match orders function has to be called here
    init {
        if(type == "BUY"){
            while(SellOrders.isNotEmpty()){
                val potentialSellOrder = SellOrders.poll()
                if(potentialSellOrder.price > price || filledQty == qty){
                    SellOrders.add(potentialSellOrder)
                    break
                }
                else{
                    val potentialSellOrderQty = min(qty-filledQty, potentialSellOrder.qty-potentialSellOrder.filledQty)

                    //Update new incoming order
                    filled.add(PriceQtyPair(potentialSellOrder.price,potentialSellOrderQty))
                    filledQty += potentialSellOrderQty
                    Users[createdBy]!!.wallet_locked -= potentialSellOrderQty * price
                    Users[createdBy]!!.wallet_free += potentialSellOrderQty * (price - potentialSellOrder.price)
                    Users[createdBy]!!.inventory_free += potentialSellOrderQty

                    //Update the potentialSellOrder that matched with this
                    potentialSellOrder.filled.add( PriceQtyPair(potentialSellOrder.price,potentialSellOrderQty))
                    potentialSellOrder.filledQty += potentialSellOrderQty
                    Users[potentialSellOrder.createdBy]!!.inventory_locked -= potentialSellOrderQty
                    Users[potentialSellOrder.createdBy]!!.wallet_free += potentialSellOrderQty * potentialSellOrder.price
                    if(potentialSellOrder.filledQty < potentialSellOrder.qty && potentialSellOrder.filledQty > 0) potentialSellOrder.status = "partially filled"
                    SellOrders.add(potentialSellOrder)
                    if(potentialSellOrder.filledQty == potentialSellOrder.qty) {
                        potentialSellOrder.status = "filled"
                        SellOrders.remove(potentialSellOrder)
                        CompletedOrders[potentialSellOrder.id] = potentialSellOrder
                    }
                }
            }
            if(filledQty == qty) {
                status = "filled"
                CompletedOrders[id] = this
            }
            else{
                if(filledQty < qty && filledQty > 0) status = "partially filled"
                BuyOrders.add(this)
            }
        }
        else if(type == "SELL"){
            while(BuyOrders.isNotEmpty()){
                val potentialBuyOrder = BuyOrders.poll()
                if(potentialBuyOrder.price < price || filledQty == qty){
                    BuyOrders.add(potentialBuyOrder)
                    break
                }
                else{
                    val potentialBuyOrderQty = min(qty-filledQty, potentialBuyOrder.qty-potentialBuyOrder.filledQty)

                    //Update new incoming order
                    filled.add(PriceQtyPair(price, potentialBuyOrderQty))
                    filledQty += potentialBuyOrderQty
                    Users[createdBy]!!.inventory_locked -= potentialBuyOrderQty
                    Users[createdBy]!!.wallet_free += potentialBuyOrderQty * price

                    //Update the order that matched with this
                    potentialBuyOrder.filled.add(PriceQtyPair(price,potentialBuyOrderQty))
                    potentialBuyOrder.filledQty += potentialBuyOrderQty
                    Users[potentialBuyOrder.createdBy]!!.wallet_locked -= potentialBuyOrderQty * potentialBuyOrder.price
                    Users[potentialBuyOrder.createdBy]!!.wallet_free += potentialBuyOrderQty * (potentialBuyOrder.price - price)
                    Users[potentialBuyOrder.createdBy]!!.inventory_free += potentialBuyOrderQty
                    if(potentialBuyOrder.filledQty < potentialBuyOrder.qty && potentialBuyOrder.filledQty > 0) potentialBuyOrder.status = "partially filled"
                    BuyOrders.add(potentialBuyOrder)
                    if(potentialBuyOrder.filledQty == potentialBuyOrder.qty) {
                        potentialBuyOrder.status = "filled"
                        BuyOrders.remove(potentialBuyOrder)
                        CompletedOrders[potentialBuyOrder.id] = potentialBuyOrder
                    }
                }
            }
            if(filledQty == qty) {
                status = "filled"
                CompletedOrders[id] = this
            }
            else{
                if(filledQty < qty && filledQty > 0) status = "partially filled"
                SellOrders.add(this)
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

