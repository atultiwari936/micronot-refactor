package com.tradingplatform.model
import java.math.BigInteger
import kotlin.math.min
import java.util.PriorityQueue
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

val esopNormal = 0
val esopPerformance = 1

data class PriceQtyPair(val price: Int, var quantity: Int) //Utility class to make the response json pretty

data class Order constructor(val type : String, val qty: Int, val price : Int, val createdBy : String, val esop_type: Int= esopNormal) {
    var status = "unfilled"
    var filled = ArrayList<PriceQtyPair>()
    val id:Pair<Int,Int> = Pair(BuyOrders.size + SellOrders.size + CompletedOrders.size*2,esop_type)
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
                else {
                    val potentialSellOrderQty =
                        min(qty - filledQty, potentialSellOrder.qty - potentialSellOrder.filledQty)


                    filled.add(PriceQtyPair(potentialSellOrder.price, potentialSellOrderQty))
                    filledQty += potentialSellOrderQty
                    Users[createdBy]!!.wallet_locked -= potentialSellOrderQty * price
                    Users[createdBy]!!.wallet_free += potentialSellOrderQty * (price - potentialSellOrder.price)
                    Users[createdBy]!!.inventory_free += potentialSellOrderQty
                    Users[createdBy]!!.pendingCreditEsop -= potentialSellOrderQty


                    potentialSellOrder.filled.add(PriceQtyPair(potentialSellOrder.price, potentialSellOrderQty))
                    potentialSellOrder.filledQty += potentialSellOrderQty

                    if (potentialSellOrder.id.second == 1) {
                        Users[potentialSellOrder.createdBy]!!.wallet_free += potentialSellOrderQty * potentialSellOrder.price
                        Users[potentialSellOrder.createdBy]!!.perf_locked -= potentialSellOrderQty
                    } else {

                        var taxAmount : Int = ceil(potentialSellOrderQty * potentialSellOrder.price*0.02).toInt()

                        Users[potentialSellOrder.createdBy]!!.wallet_free +=(potentialSellOrderQty*potentialSellOrder.price-taxAmount)
                        platformData.feesEarned += BigInteger(taxAmount.toString())
                        Users[potentialSellOrder.createdBy]!!.inventory_locked -= potentialSellOrderQty
                    }




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
                else {
                    val potentialBuyOrderQty = min(qty - filledQty, potentialBuyOrder.qty - potentialBuyOrder.filledQty)

                    filled.add(PriceQtyPair(price, potentialBuyOrderQty))
                    filledQty += potentialBuyOrderQty


                    if (id.second == 1){
                        Users[createdBy]!!.perf_locked -= potentialBuyOrderQty
                    Users[createdBy]!!.wallet_free += potentialBuyOrderQty * price
                        Users[createdBy]!!.pendingCreditAmount -= potentialBuyOrderQty * price
                    }
                    else {

                        var taxAmount : Int = ceil(potentialBuyOrderQty * price*0.02).toInt()

                        Users[createdBy]!!.wallet_free += (potentialBuyOrderQty * price - taxAmount)
                        Users[createdBy]!!.pendingCreditAmount -= (potentialBuyOrderQty * price - taxAmount)
                        platformData.feesEarned += BigInteger(taxAmount.toString())
                        Users[createdBy]!!.inventory_locked -= potentialBuyOrderQty

                    }


                    potentialBuyOrder.filled.add(PriceQtyPair(price,potentialBuyOrderQty))
                    potentialBuyOrder.filledQty += potentialBuyOrderQty
                    Users[potentialBuyOrder.createdBy]!!.wallet_locked -= potentialBuyOrderQty * price
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
        order1.id.second > order2.id.second -> -1
        order1.id.second < order2.id.second -> 1
        order1.price > order2.price -> 1
        order1.price < order2.price -> -1
        else -> {(order1.timestamp - order2.timestamp).toInt()}
        }
    }

val CompletedOrders = HashMap<Pair<Int,Int>, Order>()

data class OrderInput(
    var quantity: Int,
    val type: String,
    val price: Int,
    val esopType: String="NORMAL")

data class OrderOutput(val orderId: String,val quantity: Int,
                       val type: String,
                       val price: Int)

data class OrderHistory constructor(val type : String, val qty: Int, val price : Int, val createdBy : String, val esop_type: Int) {
    var status = "unfilled"
    var filled = ArrayList<PriceQtyPair>()
    var id: Int = 0
    lateinit var timestamp:String
    var filledQty = 0
}

data class QuantityInput(val quantity: Int,var type:String="NORMAL")
{
    var esopType=0
}

