package com.tradingplatform.controller

import com.tradingplatform.data.UserRepo
import com.tradingplatform.validations.OrderValidation
import com.tradingplatform.model.*
import com.tradingplatform.validations.UserReqValidation
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.json.tree.JsonObject
import kotlin.math.ceil
import kotlin.math.roundToInt

@Controller(value = "/user")
class OrderController {

    @Get(value = "/{userName}/order")
    fun orderHistory(@QueryValue userName: String): Any? {
        val errorList = arrayListOf<String>()
        val response = mutableMapOf<String, MutableList<String>>()
        val allOrdersOfUser: MutableList<Order> = mutableListOf()
        val user = UserRepo.getUser(userName)
        if (user !is User) {
            errorList.add("User does not exists")
            response["error"] = errorList
            return HttpResponse.badRequest(response)
        }

        val userOrderIds = user.orders

        allOrdersOfUser.addAll(getAllCompletedOrdersOfUser(userOrderIds))
        allOrdersOfUser.addAll(getAllPendingOrdersOfUser(userName))

        updateTransactionsOfOrder(allOrdersOfUser)
        return HttpResponse.ok(allOrdersOfUser)

    }

    private fun updateTransactionsOfOrder(allOrdersOfUser: MutableList<Order>) {
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


    private fun getAllCompletedOrdersOfUser(userOrderIds: ArrayList<Pair<Int, Int>>): MutableList<Order> {

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


    private fun getAllPendingOrdersOfUser(userName: String): MutableList<Order> {

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


    @Post(value = "/{userName}/order")
    fun createOrder(@Body body: JsonObject, @QueryValue userName: String): Any {
        val response = mutableMapOf<String, Any>()
        val errorList = arrayListOf<String>()
        val fieldLists = arrayListOf("quantity", "type", "price")
        for (field in fieldLists) {
            if (OrderValidation().isFieldExists(field, body)) {
                errorList.add("Enter the $field field")
            }
        }
        if (errorList.isNotEmpty()) {
            response["error"] = errorList
            return HttpResponse.badRequest(response)
        }


        if (body["quantity"] == null || !body["quantity"]!!.isNumber || ceil(body["quantity"]!!.doubleValue).roundToInt() != body["quantity"]!!.intValue) {
            errorList.add("Quantity is not valid")
        }

        if (body["price"] == null || !body["price"]!!.isNumber || ceil(body["price"]!!.doubleValue).roundToInt() != body["price"]!!.intValue) {
            errorList.add("Price is not valid")

        }

        if (body["type"] == null || !body["type"]!!.isString || (body["type"]!!.stringValue != "SELL" && body["type"]!!.stringValue != "BUY")) {

            errorList.add("Order Type is not valid")
        }
        if (errorList.isNotEmpty()) {
            response["error"] = errorList
            return HttpResponse.badRequest(response)
        }


        val quantity = body["quantity"]!!.intValue
        val type = body["type"]!!.stringValue
        val price = body["price"]!!.intValue
        val esopType = if (body["esopType"] !== null) body["esopType"]!!.stringValue else "NORMAL"



        OrderValidation().isValidQuantity(errorList, quantity)
        OrderValidation().isValidAmount(errorList, price)
        OrderValidation().isValidEsopType(errorList, esopType)


        if (errorList.isNotEmpty()) {
            response["error"] = errorList
            return HttpResponse.badRequest(response)
        }

        return orderHandler(userName, type, quantity, price, esopType)
    }

    fun orderHandler(userName: String, type: String, quantity: Int, price: Int, esopType: String = "NORMAL"): Any {
        val errorList = arrayListOf<String>()
        val response = mutableMapOf<String, Any>()
        var newOrder: Order? = null


        val errorResponse = UserReqValidation.isUserExists(userName)

        if (errorResponse != null)
            return HttpResponse.badRequest(errorResponse)

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


}