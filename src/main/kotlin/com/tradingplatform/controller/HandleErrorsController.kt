package com.tradingplatform.controller

import com.tradingplatform.exceptions.InvalidOrderException
import com.tradingplatform.exceptions.UserNotFoundException
import io.micronaut.core.convert.exceptions.ConversionErrorException
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Error
import javax.validation.ConstraintViolationException

@Controller
class HandleErrorsController {
    @Error(exception = ConstraintViolationException::class, global = true)
    fun handleTypeNotPresent(exception: ConstraintViolationException): MutableHttpResponse<Map<String, List<String>>>? {
        return HttpResponse.badRequest(mapOf("errors" to exception.constraintViolations.map { it.message }))
    }

    @Error(exception = ConversionErrorException::class, global = true)
    fun handleConversionError(exception: ConversionErrorException): MutableHttpResponse<Map<String, String>>? {
        return HttpResponse.badRequest(mapOf("errors" to "Invalid data types provided"))
    }

    @Error(exception = InvalidOrderException::class, global = true)
    fun handleOrderValidationErrors(exception: InvalidOrderException): MutableHttpResponse<Map<String, List<String>>>? {
        return HttpResponse.badRequest(mapOf("errors" to exception.errors))
    }

    @Error(exception = UserNotFoundException::class, global = true)
    fun handleUserNotFoundException(exception: UserNotFoundException): MutableHttpResponse<Map<String, List<String>>>? {
        return HttpResponse.badRequest(mapOf("errors" to exception.errors))
    }
}
