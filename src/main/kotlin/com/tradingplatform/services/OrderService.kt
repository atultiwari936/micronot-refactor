package com.tradingplatform.services

import com.tradingplatform.data.OrderRepository
import com.tradingplatform.data.UserRepository
import com.tradingplatform.dto.OrderRequest
import com.tradingplatform.dto.OrderResponse
import com.tradingplatform.dto.SellOrderResponse
import com.tradingplatform.exceptions.InvalidOrderException
import com.tradingplatform.exceptions.UserNotFoundException
import com.tradingplatform.model.*
import com.tradingplatform.validations.OrderValidation
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse

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

    fun getAllCompletedOrdersOfUser(userOrderIds: ArrayList<Int>): MutableList<Order> {

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

    fun placeOrder(userName: String, order: OrderRequest): MutableHttpResponse<OrderResponse>? {
        if (UserRepository.getUser(userName) == null) {
            throw UserNotFoundException(listOf("User doesn't exist"))
        }
        val errorList = arrayListOf<String>()
        val user = UserRepository.getUser(userName)!!
        val quantity = order.quantity!!
        val price = order.price!!
        val type = order.type!!
        var newOrder: Order?

        if (type == "BUY") {
            errorList.addAll(OrderValidation.validateBuyOrder(order, user))
            updateWalletAndInventoryForBuyOrder(user, order)

            newOrder = Order("BUY", quantity, price, user, ESOPType.NORMAL.value)
            user.orderIds.add(newOrder.id)

            orderMatchingService.matchSellOrder(newOrder)

        } else if (type == "SELL") {
            updateWalletAndInventoryForSellOrder(user, order)


            newOrder = Order("SELL", quantity, price, user, ESOPType.valueOf(order.esopType!!).value)
            user.orderIds.add(newOrder.id)

            orderMatchingService.matchBuyOrder(newOrder)
        }

        if (errorList.isNotEmpty()) {
            throw InvalidOrderException(errorList)
        }

        val response: OrderResponse =
            if (type == "SELL") SellOrderResponse(
                type = type,
                price = price,
                quantity = quantity,
                esopType = order.esopType!!
            ) else OrderResponse(
                type = type,
                price = price,
                quantity = quantity
            )
        return HttpResponse.ok(
            response
        )
    }

    private fun updateWalletAndInventoryForBuyOrder(user: User, order: OrderRequest) {
        lockBuyerWalletAmount(user, order.quantity!! * order.price!!)
        addEsopCreditToInventory(user, order.quantity!!)
    }

    private fun updateWalletAndInventoryForSellOrder(user: User, order: OrderRequest) {
        val errorList = arrayListOf<String>()

        val quantity = order.quantity!!
        val price = order.price!!

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

        if (errorList.isNotEmpty()) {
            throw InvalidOrderException(errorList)
        }
    }

    private fun lockBuyerWalletAmount(buyer: User, totalAmount: Int) {
        buyer.wallet.transferAmountFromFreeToLocked(totalAmount)
    }

    private fun addEsopCreditToInventory(buyer: User, quantity: Int) {
        buyer.inventory.addESOPToCredit(quantity)
    }

    fun updateTransactionsOfOrder(allOrdersOfUser: MutableList<Order>) {
        for (order in allOrdersOfUser) {
            val orderTransactions = order.filled
            val quantitiesAtSamePrice: MutableMap<Int, Int> = mutableMapOf()

            for (transaction in orderTransactions) {
                if (quantitiesAtSamePrice.contains(transaction.price)) {
                    quantitiesAtSamePrice[transaction.price] =
                        quantitiesAtSamePrice[transaction.price]!! + transaction.quantity
                } else {
                    quantitiesAtSamePrice[transaction.price] = transaction.quantity
                }
            }

            order.filled = quantitiesAtSamePrice.map { (price, quantity) -> PriceQuantityPair(price, quantity) } as ArrayList<PriceQuantityPair>

        }
    }
}
