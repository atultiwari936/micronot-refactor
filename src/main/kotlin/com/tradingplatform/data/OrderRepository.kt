package com.tradingplatform.data

import com.tradingplatform.comparators.BuyOrdersComparator
import com.tradingplatform.comparators.SellOrdersComparator
import com.tradingplatform.model.Order
import java.util.*
import kotlin.collections.HashMap

object OrderRepository {

    private val buyOrders = PriorityQueue(BuyOrdersComparator)

    private val sellOrders = PriorityQueue(SellOrdersComparator)

    private val completedOrders = HashMap<Int, Order>()

    fun checkIfBuyOrdersExists(): Boolean {
        return buyOrders.isNotEmpty()
    }

    fun addOrder(order: Order) {
        when (order.type) {
            "SELL" -> addSellOrder(order)
            "BUY" -> addBuyOrder(order)
            else -> throw Exception("Invalid order type")
        }
    }

    fun removeOrder(order: Order) {
        when (order.type) {
            "SELL" -> removeSellOrder(order)
            "BUY" -> removeBuyOrder(order)
            else -> throw Exception("Invalid order type")
        }
    }

    fun checkIfSellOrdersExists(): Boolean {
        return sellOrders.isNotEmpty()
    }

    fun getBuyOrders(): PriorityQueue<Order> {
        return buyOrders
    }

    fun getSellOrders(): PriorityQueue<Order> {
        return sellOrders
    }

    fun getCompletedOrders(): HashMap<Int, Order> {
        return completedOrders
    }

    fun addBuyOrder(order: Order) {
        buyOrders.add(order)
    }

    fun addSellOrder(order: Order) {
        sellOrders.add(order)
    }

    fun addCompletedOrder(order: Order) {
        completedOrders[order.id] = order
    }

    fun removeBuyOrder(order: Order) {
        buyOrders.remove(order)
    }

    fun removeSellOrder(order: Order) {
        sellOrders.remove(order)
    }

    fun getCompletedOrderById(orderID: Int): Order? {
        return completedOrders[orderID]
    }
}
