package com.tradingplatform.controller

import com.tradingplatform.data.UserRepo
import com.tradingplatform.model.Order
import com.tradingplatform.model.User
import com.tradingplatform.services.OrderService
import com.tradingplatform.validations.OrderReqValidation
import com.tradingplatform.validations.UserReqValidation
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.json.tree.JsonObject


@Controller(value = "/user")
class OrderController() {

    private val orderService: OrderService = OrderService()

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

        allOrdersOfUser.addAll(orderService.getAllCompletedOrdersOfUser(userOrderIds))
        allOrdersOfUser.addAll(orderService.getAllPendingOrdersOfUser(userName))

        orderService.updateTransactionsOfOrder(allOrdersOfUser)
        return HttpResponse.ok(allOrdersOfUser)

    }


    @Post(value = "/{userName}/order")
    fun createOrder(@Body body: JsonObject, @QueryValue userName: String): Any {
        var response: MutableMap<String, List<String>>? = UserReqValidation.isUserExists(userName)
        if (response != null)
            return HttpResponse.badRequest(response)

        response = OrderReqValidation.validateRequest(body)
        if (response != null)
            return HttpResponse.badRequest(response)

        val quantity = body["quantity"]!!.intValue
        val type = body["type"]!!.stringValue
        val price = body["price"]!!.intValue
        val esopType = if (body["esopType"] !== null) body["esopType"]!!.stringValue else "NORMAL"
        response = OrderReqValidation.isValueValid(quantity, price, esopType)
        if (response != null)
            return HttpResponse.badRequest(response)

        return orderService.orderHandler(userName, type, quantity, price, esopType)
    }


}
