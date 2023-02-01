package com.tradingplatform.controller

import OrderValidation
import UserValidation
import com.tradingplatform.model.Users
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.json.tree.JsonObject
import kotlin.math.ceil
import kotlin.math.roundToInt

@Controller(value="/user")
class WalletController {
    @Post(value = "/{userName}/wallet")
    fun addWallet(@Body body: JsonObject, @PathVariable userName:String): MutableHttpResponse<out Any>? {
        val responseMap= HashMap<String,String>()
        val errorList = arrayListOf<String>()
        val response = mutableMapOf<String, MutableList<String>>()
        response["error"] = errorList
        UserValidation().isUserExists(errorList,userName)

        val amount =body["amount"]
        if(errorList.isNotEmpty()) return HttpResponse.badRequest(response)
        if(amount==null)
        {
            errorList.add("Enter the amount field")

            return HttpResponse.badRequest(response)
        }
        if(!amount.isNumber || (ceil(amount.doubleValue).roundToInt()!=amount.intValue)) {
            errorList.add("Amount data type is invalid")
        }
        else if(OrderValidation().isValidAmount(errorList, amount.intValue, "amount"))
            OrderValidation().isWalletAmountWithinLimit(errorList, Users[userName]!!, amount.doubleValue)


        if(errorList.isNotEmpty()) return HttpResponse.badRequest(response)

        addAmountToWallet(userName,amount.intValue)
        responseMap["message"] = "${amount.intValue} added to account"
        return HttpResponse.ok(responseMap)
    }

    fun addAmountToWallet(userName: String,amount:Int)
    {
        Users[userName]?.walletFree = Users[userName]?.walletFree?.plus(amount)!!
    }


}