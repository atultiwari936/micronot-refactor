package com.tradingplatform.controller

import com.tradingplatform.model.platformData
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import java.math.BigInteger

@Controller("/platform")
class PlatformController {
    @Get("/fees")
    fun getTotalCollectedPlatfromFees():HttpResponse<*>{
        val responseMap:MutableMap<String,BigInteger> = mutableMapOf()
        responseMap["collectedFees"] = platformData.feesEarned
        return HttpResponse.ok(responseMap)
    }
}