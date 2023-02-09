package com.tradingplatform.controller

import com.fasterxml.jackson.core.JsonParseException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Error
import io.micronaut.http.hateoas.JsonError

@Controller
class InvalidJsonController {
    @Error(global = true)
    fun invalidJsonError(request: HttpRequest<*>, e: JsonParseException): Any {
        val response = mutableMapOf<String, MutableList<String>>()
        response.put("error", mutableListOf("Invalid json object"))


        return HttpResponse.badRequest<JsonError>()
            .body(response)
    }
}
