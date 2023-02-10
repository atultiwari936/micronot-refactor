package com.tradingplatform.controller

import com.tradingplatform.data.UserRepository
import com.tradingplatform.dto.OrderRequest
import com.tradingplatform.dto.OrderResponse
import com.tradingplatform.exceptions.UserNotFoundException
import com.tradingplatform.model.Order
import com.tradingplatform.model.User
import com.tradingplatform.services.OrderHistoryService
import com.tradingplatform.services.OrderService
import com.tradingplatform.validations.OrderValidation
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import javax.validation.Valid

@Validated
@Controller(value = "/user")
class OrderController {
    private val orderService: OrderService = OrderService()

    @Get(value = "/{userName}/order")
    fun orderHistory(@QueryValue userName: String): MutableHttpResponse<MutableList<Order>>? {
        val user = UserRepository.getUser(userName)
        if (user !is User) {
            throw UserNotFoundException(listOf("User doesn't exist"))
        }

        return HttpResponse.ok(OrderHistoryService.getAllOrders(user))
    }

    @Post(value = "/{userName}/order")
    fun createOrder(@Body @Valid order: OrderRequest, @QueryValue userName: String): MutableHttpResponse<OrderResponse>? {
        OrderValidation.validateOrder(order)
        return orderService.placeOrder(userName, order)
    }


}
