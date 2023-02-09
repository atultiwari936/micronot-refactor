package com.tradingplatform.data

import com.tradingplatform.model.Order
import java.util.*
import kotlin.collections.HashMap

object OrderRepository {

    private val buyOrders = PriorityQueue { order1: Order, order2: Order ->
        when {
            order1.price > order2.price -> -1
            order1.price < order2.price -> 1
            else -> {
                (order1.timestamp - order2.timestamp).toInt()
            }
        }
    }

    private val sellOrders = PriorityQueue { order1: Order, order2: Order ->
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

    private val completedOrders = HashMap<Pair<Int, Int>, Order>()

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

    fun getCompletedOrders(): HashMap<Pair<Int, Int>, Order> {
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

    fun getCompletedOrderById(orderID: Pair<Int, Int>): Order? {
        return completedOrders[orderID]
    }
}
