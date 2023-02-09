package com.tradingplatform.controller

import com.tradingplatform.data.UserRepository
import com.tradingplatform.dto.OrderRequest
import com.tradingplatform.dto.OrderResponse
import com.tradingplatform.exceptions.InvalidOrderException
import com.tradingplatform.exceptions.UserNotFoundException
import com.tradingplatform.model.Order
import com.tradingplatform.model.User
import com.tradingplatform.services.OrderService
import com.tradingplatform.validations.OrderValidation
import io.micronaut.core.convert.exceptions.ConversionErrorException
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

    @Error(exception = ConversionErrorException::class)
    fun handleConversionError(exception: ConversionErrorException): MutableHttpResponse<Map<String, String>>? {
        return HttpResponse.badRequest(mapOf("errors" to "Invalid data types provided"))
    }

    @Error(exception = InvalidOrderException::class)
    fun handleOrderValidationErrors(exception: InvalidOrderException): MutableHttpResponse<Map<String, List<String>>>? {
        return HttpResponse.badRequest(mapOf("errors" to exception.errors))
    }

    @Error(exception = UserNotFoundException::class)
    fun handleUserNotFoundException(exception: UserNotFoundException): MutableHttpResponse<Map<String, List<String>>>? {
        return HttpResponse.badRequest(mapOf("errors" to exception.errors))
    }

    @Get(value = "/{userName}/order")
    fun orderHistory(@QueryValue userName: String): Any? {
        val user = UserRepository.getUser(userName)
        if (user !is User) {
            throw UserNotFoundException(listOf("User doesn't exist"))
        }

        val allOrdersOfUser: MutableList<Order> = mutableListOf()
        val userOrderIds = user.orders

        allOrdersOfUser.addAll(orderService.getAllCompletedOrdersOfUser(userOrderIds))
        allOrdersOfUser.addAll(orderService.getAllPendingOrdersOfUser(userName))

        orderService.updateTransactionsOfOrder(allOrdersOfUser)
        return HttpResponse.ok(allOrdersOfUser)

    }

    @Post(value = "/{userName}/order")
    fun createOrder(@Body @Valid order: OrderRequest, @QueryValue userName: String): MutableHttpResponse<OrderResponse>? {
        OrderValidation.validateOrder(order)
        return orderService.placeOrder(userName, order)
    }


}
