package com.tradingplatform.controller

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Error
import io.micronaut.http.hateoas.JsonError
import io.micronaut.http.hateoas.Link

@Controller
class NotFoundController {

    @Error(status = HttpStatus.NOT_FOUND, global = true)
    fun notFound(request: HttpRequest<*>): Any {
        val response = mutableMapOf<String,MutableList<String>>()
        response.put("error", mutableListOf("Page not found"))
        val error = JsonError(response.toString())

        return HttpResponse.notFound<JsonError>()
            .body(response)
    }

}