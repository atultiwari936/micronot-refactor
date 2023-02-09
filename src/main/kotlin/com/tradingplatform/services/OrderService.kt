package com.tradingplatform.services

import com.tradingplatform.data.OrderRepository
import com.tradingplatform.data.UserRepository
import com.tradingplatform.dto.OrderRequest
import com.tradingplatform.model.*
import com.tradingplatform.validations.OrderValidation
import io.micronaut.http.HttpResponse

class OrderService {

    private val orderMatchingService: OrderMatchingService = OrderMatchingService()
    fun getAllPendingOrdersOfUser(userName: String): MutableList<Order> {
        val pendingOrdersOfUser: MutableList<Order> = mutableListOf()
        for (order in OrderRepository.getSellOrders()) {
            if (userName == order.user.userName) {
                pendingOrdersOfUser.add(order)
            }
        }
        for (order in OrderRepository.getBuyOrders()) {
            if (userName == order.user.userName) {
                pendingOrdersOfUser.add(order)
            }
        }
        return pendingOrdersOfUser
    }

    fun getAllCompletedOrdersOfUser(userOrderIds: ArrayList<Pair<Int, Int>>): MutableList<Order> {

        val completedOrdersOfUser: MutableList<Order> = mutableListOf()
        for (orderId in userOrderIds) {
            if (OrderRepository.getCompletedOrders().containsKey(orderId)) {
                val currOrder = OrderRepository.getCompletedOrderById(orderId)
                if (currOrder != null) {
                    completedOrdersOfUser.add(currOrder)
                }
            }
        }
        return completedOrdersOfUser
    }

    fun placeOrder(userName: String, order: OrderRequest): Any {
        val errorList = arrayListOf<String>()
        val response = mutableMapOf<String, Any>()
        val user = UserRepository.getUser(userName)!!
        val quantity = order.quantity!!
        val price = order.price!!
        val type = order.type!!
        var newOrder: Order? = null

        if (type == "BUY") {
            errorList.addAll(OrderValidation.validateBuyOrder(order, user))
            updateWalletAndInventoryForBuyOrder(user, order)

            newOrder = Order("BUY", quantity, price, user, ESOPType.valueOf("NORMAL").sortOrder)
            user.orders.add(newOrder.id)

            orderMatchingService.matchSellOrder(newOrder)

        } else if (type == "SELL") {
            updateWalletAndInventoryForSellOrder(user, order)

            newOrder = Order("SELL", quantity, price, user, ESOPType.valueOf(order.esopType!!).sortOrder)
            user.orders.add(newOrder.id)

            orderMatchingService.matchBuyOrder(newOrder)
        }

        response["error"] = errorList
        if (errorList.isNotEmpty()) {
            return HttpResponse.badRequest(response)
        }

        response["orderId"] = newOrder!!.id.first
        response["quantity"] = quantity
        response["type"] = type
        response["price"] = price

        return HttpResponse.ok(response)
    }

    private fun updateWalletAndInventoryForBuyOrder(user: User, order: OrderRequest) {
        lockBuyerWalletAmount(user, order.quantity!! * order.price!!)
        addEsopCreditToInventory(user, order.quantity!!)
    }

    private fun updateWalletAndInventoryForSellOrder(user: User, order: OrderRequest) {
        val quantity = order.quantity!!
        val price = order.price!!
        val errorList = arrayListOf<String>()

        if (order.esopType == "PERFORMANCE") {
            OrderValidation().isWalletAmountWithinLimit(errorList, user, price * quantity)
            OrderValidation().isSufficientPerformanceEsopsQuantity(errorList, user, quantity)

            if (errorList.isEmpty()) {
                user.inventory.addPerformanceESOPToLocked(quantity)
                user.inventory.removePerformanceESOPFromFree(quantity)
                user.wallet.addCredits(price * quantity)
            }
        } else if (order.esopType == "NORMAL") {
            OrderValidation().isWalletAmountWithinLimit(
                errorList,
                user,
                (price * quantity - PlatformData.calculatePlatformFees(price * quantity))
            )
            OrderValidation().isSufficientNonPerformanceEsopsQuantity(errorList, user, quantity)

            if (errorList.isEmpty()) {
                user.inventory.addNormalESOPToLocked(quantity)
                user.inventory.removeNormalESOPFromFree(quantity)
                user.inventory.addESOPToCredit(price * quantity - PlatformData.calculatePlatformFees(price * quantity))
            }
        }
    }

    private fun lockBuyerWalletAmount(buyer: User, totalAmount: Int) {
        buyer.wallet.transferAmountFromFreeToLocked(totalAmount)
    }

    private fun addEsopCreditToInventory(buyer: User, quantity: Int) {
        buyer.inventory.addESOPToCredit(quantity)
    }

    fun updateTransactionsOfOrder(allOrdersOfUser: MutableList<Order>) {
        for (individualOrder in allOrdersOfUser) {

            val transOfIndividualOrder = individualOrder.filled

            val transAtSamePrice: ArrayList<PriceQuantityPair> = arrayListOf()
            val transIndexAtPrice: MutableMap<Int, Int> = mutableMapOf()

            for (transPriceAndQty in transOfIndividualOrder) {
                if (transIndexAtPrice.contains(transPriceAndQty.price)) {
                    transAtSamePrice[transIndexAtPrice[transPriceAndQty.price]!!].quantity += transPriceAndQty.quantity
                } else {
                    transAtSamePrice.add(transPriceAndQty)
                    transIndexAtPrice[transPriceAndQty.price] = transAtSamePrice.size - 1
                }
            }
            individualOrder.filled = transAtSamePrice
        }
    }
}
