package com.tradingplatform.services

import com.tradingplatform.model.Order
import com.tradingplatform.model.User

object OrderHistoryService {

    private val orderService = OrderService()

    fun getAllOrders(user: User): MutableList<Order> {
        val allOrdersOfUser: MutableList<Order> = mutableListOf()
        val userOrderIds = user.orders

        allOrdersOfUser.addAll(orderService.getAllCompletedOrdersOfUser(userOrderIds))
        allOrdersOfUser.addAll(orderService.getAllPendingOrdersOfUser(user.userName))

        orderService.updateTransactionsOfOrder(allOrdersOfUser)

        return allOrdersOfUser
    }
}