package com.tradingplatform.controller

import com.tradingplatform.model.*
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Produces

@Controller("/user")
class UserController {
    @Get(value = "/register", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun register(@Body body: User): HttpResponse<User>{
        //save the user
        return HttpResponse.ok(body)
    }

    @Post(value = "/{user_name}/order")
    fun createOrder(@Body body: OrderInput): HttpResponse<OrderOutput>{
        // create order
        return HttpResponse.ok()
    }

    @Get(value = "/{user_name}/accountInformation")

    @Post(value = "/{user_name}/inventory")
    fun addInventory(@Body body: QuantityInput): HttpResponse<String>{
        //update quantity

        return HttpResponse.ok("")
    }

    @Post(value = "/{user_name}/wallet")
    fun addWallet(@Body body: WalletInput): HttpResponse<String>{
        //update wallet amount

        return HttpResponse.ok("")
    }

    @Get(value = "/{user_name}/order")  `


}