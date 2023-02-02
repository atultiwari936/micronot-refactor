package com.tradingplatform.controller

import com.fasterxml.jackson.core.JsonParseException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Error
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.http.hateoas.JsonError
import io.micronaut.http.server.exceptions.HttpServerException

@Controller
class InvalidJsonController {
    @Error(global = true)
    fun invalidJsonError(request: HttpRequest<*>, e: JsonParseException): Any {
        val response = mutableMapOf<String, MutableList<String>>()
        response.put("error", mutableListOf("Invalid json object"))
        val error = JsonError(response.toString())

        return HttpResponse.badRequest<JsonError>()
            .body(response)
    }

    @Error(global = true) //
    fun emptyJsonerror(request: HttpRequest<*>, e: Throwable): Any {
        val response = mutableMapOf<String, MutableList<String>>()
        response.put("error", mutableListOf(e.message!!))
        val error = JsonError(response.toString())

        return HttpResponse.badRequest<JsonError>()
            .body(response)
    }

}