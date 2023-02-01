package com.tradingplatform.controller

import com.tradingplatform.validations.UserValidation
import com.tradingplatform.model.*
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.json.tree.JsonObject


@Controller("/user")
class UserController {
    @Post(value = "/register", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun register(@Body body: JsonObject): MutableHttpResponse<*>? {
   
        var errorList = arrayListOf<String>()
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


        val userData = User(
            firstName = firstName,
            lastName = lastName,
            userName = userName,
            email = email.lowercase(),
            phoneNumber = phoneNumber
        )


        errorList=checkIfInputDataIsValid(userData)



        if (errorList.isNotEmpty()) {
            errorResponse["error"] = errorList
            return HttpResponse.badRequest(errorResponse)
        }



        addUser(userData)


        var okResponse = HashMap<String, String>()
        okResponse.put("message", "User Registered successfully")

        return HttpResponse.ok(okResponse)
    }


    fun checkIfInputDataIsValid(user:User) : ArrayList<String>
    {
        val errorList = arrayListOf<String>()
        UserValidation().isEmailValid(errorList,user.email)
        UserValidation().isPhoneValid(errorList, user.phoneNumber)
        UserValidation().isUserNameValid(errorList, user.userName)
        UserValidation().isNameValid(errorList, user.firstName)
        UserValidation().isNameValid(errorList, user.lastName)
        return errorList
    }







    fun addUser(userData : User)
    {
        Users[userData.userName]=userData
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

