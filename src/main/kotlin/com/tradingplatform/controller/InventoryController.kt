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
        val response = mutableMapOf<String, MutableList<String>>();
        var errorList = arrayListOf<String>()
        var msg = mutableListOf<String>()

        UserValidation().isUserExists(errorList,userName)
        if(body["quantity"]==null)
        {
            errorList.add("Quantity is missing")
            response["error"] = errorList;
            return HttpResponse.badRequest(response)
        }
        if( !body["quantity"].isNumber || ceil(body["quantity"].doubleValue).roundToInt()!=body["quantity"].intValue) {

            errorList.add("Quantity data type is invalid")
        }
        else if(OrderValidation().isValidQuantity(errorList,body["quantity"].intValue)){
            OrderValidation().isInventoryWithinLimit(errorList, Users[userName]!!,body["quantity"].intValue)
        }

        if(body["type"]!=null &&( !body["type"].isString||body["type"].stringValue!="PERFORMANCE"))
        {
            errorList.add("ESOP type is invalid ( Allowed value : PERFORMANCE and NON-PERFORMANCE)")
        }

        response["error"] = errorList;

        if(errorList.isNotEmpty()) return HttpResponse.badRequest(response)

        if(body["type"]!=null)
            msg.add(addESOPStoUserInventory(userName,"PERFORMANCE",body["quantity"].intValue))
        else
            msg.add(addESOPStoUserInventory(userName,"NORMAL",body["quantity"].intValue))

        response["message"]=msg
        return HttpResponse.ok(response)
    }

    fun addESOPStoUserInventory(userName: String,type:String,esopQuantity:Int) : String
    {
        if(type=="PERFORMANCE")
        {
            Users[userName]!!.perf_free+=esopQuantity
            return ("${esopQuantity} Performance ESOPs added to account")
        }

        Users[userName]!!.inventory_free+=esopQuantity
        return ("${esopQuantity} ESOPs added to account")
    }


}