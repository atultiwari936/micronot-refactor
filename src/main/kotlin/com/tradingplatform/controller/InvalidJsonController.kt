package com.tradingplatform.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Error
import io.micronaut.http.hateoas.JsonError

@Controller
class InvalidJsonController {
    @Error(global = true)
    fun invalidJsonError(): Any {
        val response = mutableMapOf<String, MutableList<String>>()
        response["error"] = mutableListOf("Invalid json object")

        return HttpResponse.badRequest<JsonError>()
            .body(response)
    }

    @Error(global = true) //
    fun emptyJsonError(e: Throwable): Any {
        val response = mutableMapOf<String, MutableList<String>>()
        response["error"] = mutableListOf(e.message!!)

        return HttpResponse.badRequest<JsonError>()
            .body(response)
    }

}