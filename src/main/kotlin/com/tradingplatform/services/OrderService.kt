package com.tradingplatform.services

import com.tradingplatform.data.UserRepo
import com.tradingplatform.model.*
import com.tradingplatform.validations.OrderValidation
import io.micronaut.http.HttpResponse

class OrderService {

    fun getAllPendingOrdersOfUser(userName: String): MutableList<Order> {
        val pendingOrdersOfUser: MutableList<Order> = mutableListOf()
        for (order in SellOrders) {
            if (userName == order.user.userName) {
                pendingOrdersOfUser.add(order)
            }
        }
        for (order in BuyOrders) {
            if (userName == order.user.userName) {
                pendingOrdersOfUser.add(order)
            }
        }
        return pendingOrdersOfUser
    }

    fun getAllCompletedOrdersOfUser(userOrderIds: ArrayList<Pair<Int, Int>>): MutableList<Order> {

        val completedOrdersOfUser: MutableList<Order> = mutableListOf()
        for (orderId in userOrderIds) {
            if (CompletedOrders.containsKey(orderId)) {
                val currOrder = CompletedOrders[orderId]
                if (currOrder != null) {
                    completedOrdersOfUser.add(currOrder)
                }
            }
        }
        return completedOrdersOfUser
    }

    fun orderHandler(userName: String, type: String, quantity: Int, price: Int, esopType: String = "NORMAL"): Any {
        val errorList = arrayListOf<String>()
        val response = mutableMapOf<String, Any>()
        var newOrder: Order? = null


        val user = UserRepo.getUser(userName)!!


        val totalAmount = quantity * price
        if (type == "BUY") {
            if (totalAmount > user.wallet.getFreeAmount()) {
                errorList.add("Insufficient funds in wallet")
            } else if (!user.inventory.isInventoryWithinLimit(quantity)) {
                errorList.add("Cannot place the order. Wallet amount will exceed ${PlatformData.MAX_INVENTORY_LIMIT}")
            } else {

                user.wallet.transferAmountFromFreeToLocked(totalAmount)
                user.inventory.addESOPToCredit(quantity)
                newOrder = Order("BUY", quantity, price, user, esopNormal)
                user.orders.add(newOrder.id)

            }
        } else if (type == "SELL") {
            if (esopType == "PERFORMANCE") {
                if (quantity > user.inventory.getPerformanceFreeQuantity()) {
                    errorList.add("Insufficient Performance ESOPs in inventory")
                } else if (!OrderValidation().isWalletAmountWithinLimit(
                        errorList, user, price * quantity
                    )
                )
                else {
                    user.inventory.addPerformanceESOPToLocked(quantity)
                    user.inventory.removePerformanceESOPFromFree(quantity)
                    user.wallet.credit += totalAmount

                    newOrder = Order("SELL", quantity, price, user, esopPerformance)
                    user.orders.add(newOrder.id)

                }
            } else if (esopType == "NORMAL") {
                if (quantity > user.inventory.getNormalFreeQuantity()) {
                    errorList.add("Insufficient Normal ESOPs in inventory")
                } else if (!OrderValidation().isWalletAmountWithinLimit(
                        errorList, user, (price * quantity * 0.98).toInt()
                    )
                )
                else {
                    user.inventory.addNormalESOPToLocked(quantity)
                    user.inventory.removeNormalESOPFromFree(quantity)
                    user.wallet.credit += (totalAmount * 0.98).toInt()

                    newOrder = Order("SELL", quantity, price, user, esopNormal)
                    user.orders.add(newOrder.id)
                }
            }
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

    fun updateTransactionsOfOrder(allOrdersOfUser: MutableList<Order>) {
        for (individualOrder in allOrdersOfUser) {

            val transOfIndividualOrder = individualOrder.filled

            val transAtSamePrice: ArrayList<PriceQtyPair> = arrayListOf()
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
