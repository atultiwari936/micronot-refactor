package com.tradingplatform.controller

import com.tradingplatform.data.UserRepo
import com.tradingplatform.dto.OrderRequest
import com.tradingplatform.model.Order
import com.tradingplatform.model.User
import com.tradingplatform.services.OrderService
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import javax.validation.ConstraintViolationException
import javax.validation.Valid

@Validated
@Controller(value = "/user")
class OrderController {
    private val orderService: OrderService = OrderService()

    @Error(exception = ConstraintViolationException::class)
    fun handleTypeNotPresent(exception: ConstraintViolationException): MutableHttpResponse<Map<String, List<String>>>? {
        return HttpResponse.badRequest(mapOf("errors" to exception.constraintViolations.map { it.message }))
    }



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
    fun createOrder(@Body @Valid dto: OrderRequest , @QueryValue userName: String): Any {
        return HttpResponse.ok("Hi")
//        var response: MutableMap<String, List<String>>? = UserReqValidation.isUserExists(userName)
//        if (response != null)
//            return HttpResponse.badRequest(response)
//
//        response = OrderReqValidation.validateRequest(order)
//        if (response != null)
//            return HttpResponse.badRequest(response)
//
//        response = OrderReqValidation.isValueValid(quantity, price, esopType)
//        if (response != null)
//            return HttpResponse.badRequest(response)
//
//        return orderService.orderHandler(userName, type, quantity, price, esopType)
    }


}
