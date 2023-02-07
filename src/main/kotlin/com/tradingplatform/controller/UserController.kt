package com.tradingplatform.controller

import com.tradingplatform.data.UserRepo
import com.tradingplatform.model.*
import com.tradingplatform.validations.UserReqValidation
import com.tradingplatform.validations.UserValidation
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
        val errorResponse = mutableMapOf<String, MutableList<String>>()

        val response=UserReqValidation.ifValidFields(body)
        if (response != null) {
            return HttpResponse.badRequest(response)
        }

        val userName = body["userName"]!!.stringValue
        val phoneNumber = body["phoneNumber"]!!.stringValue
        val firstName = body["firstName"]!!.stringValue
        val lastName = body["lastName"]!!.stringValue
        val email = body["email"]!!.stringValue
        val userData = User(
            firstName = firstName,
            lastName = lastName,
            userName = userName,
            email = email.lowercase(),
            phoneNumber = phoneNumber
        )

        errorList = checkIfInputDataIsValid(userData)

        if (errorList.isNotEmpty()) {
            errorResponse["error"] = errorList
            return HttpResponse.badRequest(errorResponse)
        }

        UserRepo.addUser(userData)

        val okResponse = HashMap<String, String>()
        okResponse["message"] = "User registered successfully"

        return HttpResponse.ok(okResponse)
    }

    fun checkIfInputDataIsValid(user: User): ArrayList<String> {
        val errorList = arrayListOf<String>()
        errorList.addAll(UserValidation().isEmailValid(user.email))
        UserValidation().isPhoneValid(errorList, user.phoneNumber)
        UserValidation().isUserNameValid(errorList, user.userName)
        UserValidation().isNameValid(errorList, user.firstName)
        UserValidation().isNameValid(errorList, user.lastName)
        return errorList
    }


    @Get(value = "/{userName}/accountInformation")
    fun getAccountInformation(@PathVariable(name = "userName") userName: String): MutableHttpResponse<out Any?>? {

        val response = mutableMapOf<String, Any>()
        val errorResponse = UserReqValidation.isUserExists(userName)

        if (errorResponse != null)
            return HttpResponse.badRequest(errorResponse)

        val user = UserRepo.getUser(userName)!!

        val wallet = mutableMapOf<String, Int>()
        wallet["free"] = user.wallet.getFreeAmount()
        wallet["locked"] = user.wallet.getLockedAmount()

        val inventory = mutableListOf<InventoryOutput>()

        val normalInventory =
            InventoryOutput(user.inventory.esopNormal.free, user.inventory.esopNormal.locked, "NON_PERFORMANCE")
        val performanceInventory =
            InventoryOutput(user.inventory.esopPerformance.free, user.inventory.esopPerformance.locked, "PERFORMANCE")

        inventory.add(normalInventory)
        inventory.add(performanceInventory)
        response["firstName"] = user.firstName
        response["lastName"] = user.lastName
        response["phoneNumber"] = user.phoneNumber
        response["email"] = user.email
        response["wallet"] = wallet
        response["inventory"] = inventory

        return HttpResponse.ok(response)
    }
}