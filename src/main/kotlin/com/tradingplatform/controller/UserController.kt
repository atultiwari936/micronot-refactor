package com.tradingplatform.controller

import com.tradingplatform.model.*
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import com.tradingplatform.model.Users
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import java.io.Serializable
import java.lang.Integer.min

//
@Controller("/user")
class UserController {
    @Post(value = "/register", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun register(@Body body: Register): MutableHttpResponse<out Any?>? {
        //error list
        val errorList = arrayListOf<String>()
        val response = mutableMapOf<String, MutableList<String>>();

        val email = body.email
        val userName = body.userName
        val phoneNumber = body.phoneNumber
        val firstName = body.firstName
        val lastName = body.lastName

        val emailRegex="^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$"
        val userNameRegex="^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){0,18}\$"
        val nameRegex="^[a-zA-z ]*\$"
        val phoneNumberRegex="^[0-9]{10}\$"
        //check for username, email and phone number
        if(!(email.isNotEmpty()&&emailRegex.toRegex().matches(email)))
        {
            errorList.add("Invalid Email format")
        }
        if(!(userName.isNotEmpty() && userNameRegex.toRegex().matches(userName)))
        {
            errorList.add("Invalid UserName format")
        }
        if(!(phoneNumber.isNotEmpty()&&phoneNumberRegex.toRegex().matches(phoneNumber)&&phoneNumber[0]!='0'))
        {
            errorList.add("Invalid phone number")
        }
        if(!(firstName.isNotEmpty()&&nameRegex.toRegex().matches(firstName)))
        {
            errorList.add("First Name is not in valid format")
        }
        if(!(lastName.isNotEmpty()&&nameRegex.toRegex().matches(lastName)))
        {
            errorList.add("Last Name is not in valid format")
        }

        //Username consists of alphanumeric characters (a-zA-Z0-9), lowercase, or uppercase.
//            Username allowed of the dot (.), underscore (_), and hyphen (-).
//            The dot (.), underscore (_), or hyphen (-) must not be the first or last character.
//            The dot (.), underscore (_), or hyphen (-) does not appear consecutively, e.g., java..regex
//            The number of characters must be between 5 to 20.

//            It allows numeric values from 0 to 9.
//            Both uppercase and lowercase letters from a to z are allowed.
//            Allowed are underscore “_”, hyphen “-“, and dot “.”
//            Dot isn't allowed at the start and end of the local part.
//            Consecutive dots aren't allowed.
//            For the local part, a maximum of 64 characters are allowed.
        for((key, user) in Users) {
            if (user.email == email){
                errorList.add("Email already exist")
            }
            if(user.userName == userName){
                errorList.add("Username already exist")
            }
            if (user.phoneNumber == phoneNumber) {
                errorList.add("Phone number already exist")
            }
        }
        //if error list is not empty
        if(errorList.isNotEmpty()){
            response["error"] = errorList;
            return HttpResponse.badRequest(response)
        }

        // if no error then just push user obj to the hashmap


        Users[userName] = User(firstName = firstName,
            lastName = lastName,
            userName = userName,
            email = email.lowercase(),
            phoneNumber = phoneNumber
        )

        return HttpResponse.ok(Register(firstName = firstName,
            lastName = lastName,
            email=email,
            phoneNumber= phoneNumber,
            userName = userName))
    }

    @Post(value = "/{user_name}/order")
    fun createOrder(@Body body: OrderInput, @QueryValue user_name: String): Any {
        val errorList = arrayListOf<String>()
        var newOrder : Order? = null
        if(Users.containsKey(user_name)){
            val user = Users[user_name]!!
            if(body.type == "BUY"){
                if(body.quantity * body.price > user.wallet_free) errorList.add("Insufficient funds in wallet")
                else{
                    user.wallet_free -= body.quantity * body.price
                    user.wallet_locked += body.quantity * body.price
                    newOrder = Order("BUY", body.quantity, body.price, user_name, 3)
                    user.orders.add(newOrder.id)

                }
            }
            else if(body.type == "SELL"){
                if(body.quantity > user.inventory_free) errorList.add("Insufficient ESOPs in inventory")
                else{
                    if(user.perf_free > 0 && body.esopType == "PERFORMANCE"){
                        val perfQuantity=min(user.perf_free,body.quantity)
                        user.perf_locked+=perfQuantity
                        user.perf_free-=perfQuantity
                        body.quantity-=perfQuantity
                        newOrder = Order("SELL",perfQuantity, body.price, user_name,1)
                        user.orders.add(newOrder.id)

                    }

                    if(user.inventory_free > 0 && body.esopType == "NORMAL"){
                        val nperfQuantity=min(user.inventory_free,body.quantity)
                        user.inventory_locked+=nperfQuantity
                        user.inventory_free-=nperfQuantity
                        body.quantity-=nperfQuantity
                        newOrder = Order("SELL",nperfQuantity, body.price, user_name,0)
                        user.orders.add(newOrder.id)


                    }
                }
            }
            else
                errorList.add("Invalid type given")
        }
        else errorList.add("User doesn't exist")
        if(errorList.isNotEmpty()) return HttpResponse.badRequest(errorList)
        // check if quantity and amount is sufficient or not
        // create order
        return HttpResponse.ok(newOrder)
    }

    @Get(value = "/{userName}/accountInformation")
    fun getAccountInformation(@PathVariable(name="userName")userName: String): MutableHttpResponse<out Any?>? {
        var response = mutableMapOf<String, Any>()

        if (!Users.containsKey(userName)) {
            response["error"] = "User doesn't exist"
            return HttpResponse.badRequest(response)
        }

        val user = Users[userName]

        var wallet = mutableMapOf<String, Int>()
        wallet["free"] = user!!.wallet_free
        wallet["locked"] = user!!.wallet_locked

        var inventory = mutableMapOf<String, Int>()
        inventory["free"] = user!!.inventory_free
        inventory["locked"] = user!!.inventory_locked

        response["firstName"] = user!!.firstName
        response["lastName"] = user!!.lastName
        response["phoneNumber"] = user!!.phoneNumber
        response["email"] = user!!.email
        response["wallet"] = wallet
        response["inventory"] = inventory

        return HttpResponse.ok(response)
    }

    @Post(value = "/{userName}/inventory")
    fun addInventory(@Body body: QuantityInput, @PathVariable(name="userName")userName: String): MutableHttpResponse<out Any>? {
        //update quantity


        val response = mutableMapOf<String, MutableList<String>>();
        var errorList = mutableListOf<String>()
        var msg = mutableListOf<String>()
        if(!Users.containsKey(userName))
        {
            errorList.add("User does not exist")
            response["error"] = errorList;
            return HttpResponse.badRequest(response)
        }
        //check here
        if (body.quantity<=0 || body.quantity>2147483640)
        {
            errorList.add("Enter a valid ESOP quantity")
            response["error"] = errorList;
            return HttpResponse.badRequest(response)

        }


        Users[userName]?.inventory_free = Users[userName]?.inventory_free?.plus(body.quantity)!!
        msg.add("${body.quantity} ESOPs added to account")

        response["message"]=msg
        return HttpResponse.ok(response)
    }
    @Post(value = "/{userName}/wallet")
    fun addWallet(@Body body: WalletInput, @PathVariable(name = "userName")userName:String): MutableHttpResponse<out Any>? {
        //update wallet amount
        var responseMap= HashMap<String,String>()
        val errorList = arrayListOf<String>()

        val response = mutableMapOf<String, MutableList<String>>();

        if(!Users.containsKey(userName))
        {
            errorList.add("User does not exist")
            response["error"] = errorList;
            return HttpResponse.badRequest(response)
        }
        ///check here
        if(body.amount<=0 || body.amount>2147483640)
        {
            errorList.add("Enter a valid amount")
            response["error"] = errorList;
            return HttpResponse.badRequest(response)

        }

        Users[userName]?.wallet_free = Users[userName]?.wallet_free?.plus(body.amount)!!
        responseMap.put("message","${body.amount} added to account")
        return HttpResponse.ok(responseMap)

    }
    @Get(value = "/{userName}/order")
    fun getOrder(@QueryValue userName: String): Any? {
        val errorList = arrayListOf<String>()
        if(!Users.containsKey(userName))
        {
            errorList.add("User does not exist")
            return HttpResponse.badRequest(errorList)
        }

        val userOrders = arrayListOf<Order>()
        for(order in BuyOrders){
            if(userName == order.createdBy){
                userOrders.add(order)
            }
        }

        for(order in SellOrders){
            if(userName == order.createdBy){
                userOrders.add(order)
            }
        }

        val userOrderIds = Users[userName]!!.orders
        for(orderId in userOrderIds){
            if(CompletedOrders.containsKey(orderId)){
                userOrders.add(CompletedOrders[orderId]!!)
            }
        }

        

        return HttpResponse.ok(userOrders)
    }


}