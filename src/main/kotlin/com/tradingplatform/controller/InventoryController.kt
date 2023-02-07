package com.tradingplatform.controller

import com.tradingplatform.data.UserRepo
import com.tradingplatform.model.PlatformData
import com.tradingplatform.model.User
import com.tradingplatform.validations.OrderValidation
import com.tradingplatform.validations.UserReqValidation
import com.tradingplatform.validations.UserValidation
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.json.tree.JsonObject
import kotlin.math.ceil
import kotlin.math.roundToInt


@Controller(value = "user/{userName}")
class InventoryController {
    @Post(value = "/inventory")
    fun addInventory(
        @Body body: JsonObject,
        @PathVariable(name = "userName") userName: String
    ): MutableHttpResponse<out Any>? {
        val response = mutableMapOf<String, MutableList<String>>()
        val msg = mutableListOf<String>()
        val errorList = ArrayList<String>()

        val errorResponse = UserReqValidation.isUserExists(userName)
        if (errorResponse != null)
            return HttpResponse.badRequest(errorResponse)

        val user = UserRepo.getUser(userName)!!

        val quantity = body["quantity"]
        if (quantity == null) {
            errorList.add("Quantity is missing")
            response["error"] = errorList
            return HttpResponse.badRequest(response)
        }
        if (!quantity.isNumber || ceil(quantity.doubleValue).roundToInt() != quantity.intValue) {

            errorList.add("Quantity data type is invalid")
        } else if (OrderValidation().isValidQuantity(errorList, quantity.intValue)) {
            if (!user.inventory.isInventoryWithinLimit(quantity.intValue)) {
                errorList.add("Cannot place the order. Wallet amount will exceed ${PlatformData.MAX_INVENTORY_LIMIT}")
            }
        }

        val type = body["type"]

        if (type != null && (!type.isString || type.stringValue != "PERFORMANCE")) {
            errorList.add("ESOP type is invalid ( Allowed value : PERFORMANCE and NON-PERFORMANCE)")
        }

        response["error"] = errorList
        if (errorList.isNotEmpty()) return HttpResponse.badRequest(response)

        if (type != null)
            msg.add(addESOPStoUserInventory(user, "PERFORMANCE", quantity.intValue))
        else
            msg.add(addESOPStoUserInventory(user, "NORMAL", quantity.intValue))

        response["message"] = msg
        return HttpResponse.ok(response)
    }

    fun addESOPStoUserInventory(user: User, type: String, esopQuantity: Int): String {

        if (type == "PERFORMANCE") {
            user.inventory.addPerformanceESOPToFree(esopQuantity)
            return ("$esopQuantity $type ESOPs added to account")
        }

        user.inventory.addNormalESOPToFree(esopQuantity)
        return ("$esopQuantity ESOPs added to account")

    }
}