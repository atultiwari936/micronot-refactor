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
        val response = mutableMapOf<String, MutableList<String>>();
        response["error"] = errorList;
        UserValidation().isUserExists(errorList,userName)


        if(errorList.isNotEmpty()) return HttpResponse.badRequest(response)
        if(body["amount"]==null)
        {
            errorList.add("Enter the amount field")

            return HttpResponse.badRequest(response)
        }
        if(!body["amount"].isNumber || (ceil(body["amount"].doubleValue).roundToInt()!=body["amount"].intValue)) {
            errorList.add("Amount data type is invalid")
        }
        else if(OrderValidation().isValidAmount(errorList, body["amount"].intValue, "amount"))
            OrderValidation().isWalletAmountWithinLimit(errorList, Users[userName]!!, body["amount"].doubleValue)


        if(errorList.isNotEmpty()) return HttpResponse.badRequest(response)

        Users[userName]?.wallet_free = Users[userName]?.wallet_free?.plus(body["amount"].intValue)!!
        responseMap["message"] = "${body["amount"].intValue} added to account"
        return HttpResponse.ok(responseMap)
    }

}