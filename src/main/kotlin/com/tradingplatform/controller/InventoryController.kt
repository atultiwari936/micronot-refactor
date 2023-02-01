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


@Controller(value = "user/{userName}")
class InventoryController {
    @Post(value="/inventory")
    fun addInventory(@Body body: JsonObject, @PathVariable(name="userName")userName: String): MutableHttpResponse<out Any>? {
        val response = mutableMapOf<String, MutableList<String>>()
        val msg = mutableListOf<String>()
        val errorList=checkIfUserExist(userName)


        response["error"] = errorList
        if(errorList.isNotEmpty()) return HttpResponse.badRequest(response)

        val quantity = body["quantity"]
        if(quantity ==null)
        {
            errorList.add("Quantity is missing")
            response["error"] = errorList
            return HttpResponse.badRequest(response)
        }
        if(!quantity.isNumber || ceil(quantity.doubleValue).roundToInt()!= quantity.intValue) {

            errorList.add("Quantity data type is invalid")
        }
        else if(OrderValidation().isValidQuantity(errorList, quantity.intValue)){
            OrderValidation().isInventoryWithinLimit(errorList, Users[userName]!!, quantity.intValue)
        }

        val type = body["type"]

        if(type !=null &&( !type.isString|| type.stringValue!="PERFORMANCE"))
        {
            errorList.add("ESOP type is invalid ( Allowed value : PERFORMANCE and NON-PERFORMANCE)")
        }

        response["error"] = errorList

        if(errorList.isNotEmpty()) return HttpResponse.badRequest(response)

        if(type !=null)
            msg.add(addESOPStoUserInventory(userName,"PERFORMANCE", quantity.intValue))
        else
            msg.add(addESOPStoUserInventory(userName,"NORMAL", quantity.intValue))

        response["message"]=msg
        return HttpResponse.ok(response)
    }

    fun addESOPStoUserInventory(userName: String,type:String,esopQuantity:Int) : String
    {
        if(type=="PERFORMANCE")
        {
            Users[userName]!!.perf_free+=esopQuantity
            return ("$esopQuantity Performance ESOPs added to account")
        }

        Users[userName]!!.inventory_free+=esopQuantity
        return ("$esopQuantity ESOPs added to account")
    }

    fun checkIfUserExist(userName: String): ArrayList<String> {
        val errorList = arrayListOf<String>()
        UserValidation().isUserExists(errorList,userName)
        return errorList
    }


}