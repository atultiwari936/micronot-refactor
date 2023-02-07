package com.tradingplatform.controller

import com.tradingplatform.data.UserRepo
import com.tradingplatform.model.User
import com.tradingplatform.validations.OrderValidation
import com.tradingplatform.validations.UserReqValidation
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.json.tree.JsonObject
import kotlin.math.ceil
import kotlin.math.roundToInt

@Controller(value = "/user")
class WalletController {
    @Post(value = "/{userName}/wallet")
    fun addWallet(@Body body: JsonObject, @PathVariable userName: String): MutableHttpResponse<out Any>? {
        val responseMap = HashMap<String, String>()
        val errorList = arrayListOf<String>()
        val response = mutableMapOf<String, MutableList<String>>()


        val errorResponse = UserReqValidation.isUserExists(userName)
        if (errorResponse != null)
            return HttpResponse.badRequest(errorResponse)
        val user = UserRepo.getUser(userName)!!

        val amount = body["amount"]
        if (amount == null) {
            response["error"] = errorList
            errorList.add("Enter the amount field")
            return HttpResponse.badRequest(response)
        }

        if (!amount.isNumber || (ceil(amount.doubleValue).roundToInt() != amount.intValue)) {
            errorList.add("Amount data type is invalid")
        } else if (OrderValidation().isValidAmount(errorList, body["amount"].intValue))
            OrderValidation().isWalletAmountWithinLimit(errorList, user, body["amount"].intValue)

        response["error"] = errorList
        if (errorList.isNotEmpty()) return HttpResponse.badRequest(response)

        user.wallet.addAmountToFree(amount.intValue)
        responseMap["message"] = "${amount.intValue} added to account"
        return HttpResponse.ok(responseMap)
    }

}