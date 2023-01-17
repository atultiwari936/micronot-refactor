package com.tradingplatform.controller

import com.tradingplatform.model.*
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Produces
import com.tradingplatform.model.Users
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.QueryValue

@Controller("/user")
class UserController {
    @Post(value = "/register", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun register(@Body body: Register): MutableHttpResponse<out Any?>? {
        //error list
        val errorList = arrayListOf<String>()

        val email = body.email
        val userName = body.userName
        val phoneNumber = body.phoneNumber

        //check for username, email and phone number
        for((key, user) in Users) {
            if(user.email == email){
                errorList.add("Email already exist")
            }
            if(user.userName == userName){
                errorList.add("Username already exist")
            }
            if(user.phoneNumber == phoneNumber) {
                errorList.add("Phone number already exist")
            }
        }
        //if error list is not empty
        if(errorList.isNotEmpty()){
            return HttpResponse.badRequest(errorList)
        }

        // if no error then just push user obj to the hashmap
        val firstName = body.firstName
        val lastName = body.lastName

        Users[userName] = User(firstName = firstName,
            lastName = lastName,
            userName = userName,
            email = email,
            phoneNumber = phoneNumber
        )

        return HttpResponse.ok(Register(firstName = firstName,
            lastName = lastName,
            email=email,
            phoneNumber= phoneNumber,
            userName = userName))
    }

    @Post(value = "/{user_name}/order")
    fun createOrder(@Body body: OrderInput, @QueryValue user_name: String): HttpResponse<OrderOutput>{

        // check if quantity and amount is sufficient or not
        // create order
        return HttpResponse.ok()
    }

    @Get(value = "/{user_name}/accountInformation")
    fun getAccountInformation(@Body body: String, @QueryValue user_name: String): HttpResponse<String>{
        return HttpResponse.ok("")
    }

    @Post(value = "/{user_name}/inventory")
    fun addInventory(@Body body: QuantityInput, @QueryValue user_name: String): HttpResponse<String>{
        //update quantity

        return HttpResponse.ok("")
    }

    @Post(value = "/{user_name}/wallet")
    fun addWallet(@Body body: WalletInput, @QueryValue user_name: String): HttpResponse<String>{
        //update wallet amount

        return HttpResponse.ok("")
    }

    @Get(value = "/{user_name}/order")
    fun getOrder(@Body body: String, @QueryValue user_name: String): HttpResponse<String>{
        return HttpResponse.ok("")
    }


}