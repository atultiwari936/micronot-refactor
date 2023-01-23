package com.tradingplatform.controller

import OrderValidation
import UserValidation
import com.tradingplatform.model.*
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.json.tree.JsonObject
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.ceil
import kotlin.math.roundToInt


@Controller("/user")
class UserController {
    @Post(value = "/register", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun register(@Body body: JsonObject): MutableHttpResponse<*>? {
   
        val errorList = arrayListOf<String>()
        val errorResponse = mutableMapOf<String, MutableList<String>>();
        var fieldLists= arrayListOf<String>("userName","firstName","lastName","phoneNumber","email")

        //Check for empty fields
        for (field in fieldLists) {
            if (UserValidation().isFieldExists(field, body)) {
                errorList.add("Enter the $field field")
                errorResponse["error"] = errorList
            }else if(body[field]==null||!body[field].isString)
            {
                errorList.add("$field Data type not in valid format")
                errorResponse["error"] = errorList
            }
        }
        
        if (errorList.isNotEmpty()) {
            return HttpResponse.badRequest(errorResponse)
        }

        val userName = body["userName"].stringValue
        val phoneNumber = body["phoneNumber"].stringValue
        val firstName = body["firstName"].stringValue
        val lastName = body["lastName"].stringValue
        val email = body["email"].stringValue

        //Validations on all
        UserValidation().isEmailValid(errorList, email)
        UserValidation().isPhoneValid(errorList, phoneNumber)
        UserValidation().isUserNameValid(errorList, userName)
        UserValidation().isNameValid(errorList, firstName)
        UserValidation().isNameValid(errorList, lastName)


        if (errorList.isNotEmpty()) {
            errorResponse["error"] = errorList
            return HttpResponse.badRequest(errorResponse)
        }

        Users[userName] = User(
            firstName = firstName,
            lastName = lastName,
            userName = userName,
            email = email.lowercase(),
            phoneNumber = phoneNumber
        )

        var okResponse = HashMap<String, String>()
        okResponse.put("message", "User Registered successfully")

        return HttpResponse.ok(okResponse)
    }


    @Get(value = "/{userName}/accountInformation")
    fun getAccountInformation(@PathVariable(name="userName")userName: String): MutableHttpResponse<out Any?>? {

        var response = mutableMapOf<String,Any>();
        var errorList = arrayListOf<String>()
        UserValidation().isUserExists(errorList,userName)
        if(errorList.isNotEmpty()){
            response["error"] = errorList;

            return HttpResponse.badRequest(response)
        }

        val user = Users[userName]

        var wallet = mutableMapOf<String, Int>()
        wallet["free"] = user!!.wallet_free
        wallet["locked"] = user!!.wallet_locked

        var inventory = mutableListOf<InventoryOutput>()

        val normal_inventory = InventoryOutput(user!!.inventory_free, user!!.inventory_locked, "NON_PERFORMANCE")
        val performance_inventory = InventoryOutput(user!!.perf_free, user!!.perf_locked, "PERFORMANCE")

        inventory.add(normal_inventory)
        inventory.add(performance_inventory)
        response["firstName"] = user!!.firstName
        response["lastName"] = user!!.lastName
        response["phoneNumber"] = user!!.phoneNumber
        response["email"] = user!!.email
        response["wallet"] = wallet
        response["inventory"] = inventory

        return HttpResponse.ok(response)
    }






}


data class OrderHistory constructor(val type : String, val qty: Int, val price : Int, val createdBy : String, val esop_type: Int) {
    var status = "unfilled"
    var filled = ArrayList<PriceQtyPair>()
    var id: Int = 0
    lateinit var timestamp:String
    var filledQty = 0
}

