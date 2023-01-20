package com.tradingplatform.controller

import OrderValidation
import UserValidation
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
import io.micronaut.json.tree.JsonObject
import java.lang.Integer.min


//
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
            }
        }
        if (errorList.isNotEmpty()) {
            return HttpResponse.badRequest(errorResponse)
        }

        val userName = body["userName"].stringValue
        val phoneNumber = body["phoneNumber"].stringValue
        val firstName = body["firstName"].stringValue
        val lastName = body["lastName"].stringValue
        val email=body["email"].stringValue

        //Validations on all
        UserValidation().isEmailValid(errorList,email)
        UserValidation().isPhoneValid(errorList,phoneNumber)
        UserValidation().isUserNameValid(errorList,userName)
        UserValidation().isNameValid(errorList,firstName)
        UserValidation().isNameValid(errorList,lastName)


        if (errorList.isNotEmpty()) {
            errorResponse["error"] = errorList
            return HttpResponse.badRequest(errorResponse)
        }

        Users[userName] = User(firstName = firstName,
            lastName = lastName,
            userName = userName,
            email =email.lowercase(),
            phoneNumber = phoneNumber
        )

        var okResponse=HashMap<String,String>()
        okResponse.put("message","User Registered successfully")

        return HttpResponse.ok(okResponse)
    }

    @Post(value = "/{user_name}/order")
    fun createOrder(@Body body: OrderInput, @QueryValue user_name: String): Any {
        val response = mutableMapOf<String, MutableList<String>>();
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
        response["error"] = errorList
        if(errorList.isNotEmpty()) return HttpResponse.badRequest(response)
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
        
        if(body.type!="NORMAL")
        {
            body.esopType=1
        }
        if(body.esopType==0) {
            Users[userName]?.inventory_free = Users[userName]?.inventory_free?.plus(body.quantity)!!
            msg.add("${body.quantity} ESOPs added to account")
        }
        else {
            Users[userName]?.perf_free = Users[userName]?.perf_free?.plus(body.quantity)!!
            msg.add("${body.quantity} Performance ESOPs added to account")
        }



        response["message"]=msg
        return HttpResponse.ok(response)
    }
    
    @Post(value = "/{userName}/wallet")
    fun addWallet(@Body body: WalletInput, @PathVariable userName:String): MutableHttpResponse<out Any>? {

        val responseMap= HashMap<String,String>()
        val errorList = arrayListOf<String>()
        val response = mutableMapOf<String, MutableList<String>>();
        UserValidation().isUserExists(errorList,userName)
        OrderValidation().isValidAmount(errorList,body.amount)
        response["error"] = errorList;
        if(errorList.isNotEmpty()) return HttpResponse.badRequest(response)

        Users[userName]?.wallet_free = Users[userName]?.wallet_free?.plus(body.amount)!!
        responseMap["message"] = "${body.amount} added to account"


        return HttpResponse.ok(responseMap)
    }



    @Get(value = "/{userName}/order")
    fun getOrder(@QueryValue userName: String): Any? {
        val errorList = arrayListOf<String>()
        val response = mutableMapOf<String, MutableList<String>>();
        if(!Users.containsKey(userName))
        {
            errorList.add("User does not exist")
        }

        var userOrders: HashMap<Int,OrderHistory> = hashMapOf()


        val userOrderIds = Users[userName]!!.orders

        for(orderId in userOrderIds){

            if(CompletedOrders.containsKey(orderId)){

                if(!userOrders.contains(orderId.first))
                {
                    var currOrder=CompletedOrders.get(orderId);
                    var x : OrderHistory= OrderHistory(currOrder!!.type,currOrder.qty,currOrder.price,currOrder.createdBy, currOrder.esop_type)
                    x.id=currOrder.id.first
                    x.status="filled"
                    x.timestamp=currOrder.timestamp
                    x.filledQty=currOrder.filledQty
                    x.filled=currOrder.filled

                    userOrders.put(x.id,x)
                }
                else
                {
                    var currOrder=userOrders[orderId.first]
                    var now=CompletedOrders.get(orderId);

                    currOrder!!.filledQty+=now!!.filledQty
                    currOrder!!.filled.addAll(now.filled)
                }
            }
        }


        for(order in BuyOrders){
            if(userName == order.createdBy){

                var orderId=order.id


                if(!userOrders.contains(orderId.first))
                {
                    var currOrder=CompletedOrders.get(orderId);
                    var x : OrderHistory= OrderHistory(currOrder!!.type,currOrder.qty,currOrder.price,currOrder.createdBy, currOrder.esop_type)
                    x.id=currOrder.id.first
                    x.status="unfilled"
                    x.timestamp=currOrder.timestamp
                    x.filledQty=currOrder.filledQty
                    x.filled=currOrder.filled

                    userOrders.put(x.id,x)
                }
                else
                {
                    var currOrder=userOrders[orderId.first]
                    var now=CompletedOrders.get(orderId);

                    if(currOrder!!.status=="filled")
                        currOrder.status="partially filled"

                    currOrder!!.filledQty+=now!!.filledQty
                    currOrder!!.filled.addAll(now.filled)
                }





            }
        }

        for(order in SellOrders){
            if(userName == order.createdBy){
                var orderId=order.id


                if(!userOrders.contains(orderId.first))
                {
                    var currOrder=CompletedOrders.get(orderId);
                    var x : OrderHistory= OrderHistory(currOrder!!.type,currOrder.qty,currOrder.price,currOrder.createdBy, currOrder.esop_type)
                    x.id=currOrder.id.first
                    x.status="unfilled"
                    x.timestamp=currOrder.timestamp
                    x.filledQty=currOrder.filledQty
                    x.filled=currOrder.filled

                    userOrders.put(x.id,x)
                }
                else
                {
                    var currOrder=userOrders[orderId.first]
                    var now=CompletedOrders.get(orderId);

                    if(currOrder!!.status=="filled")
                        currOrder.status="partially filled"

                    currOrder!!.filledQty+=now!!.filledQty
                    currOrder!!.filled.addAll(now.filled)
                }

            }
        }

        if(errorList.isNotEmpty()){
            response["error"] = errorList;
            return HttpResponse.badRequest(response)
        }
        var listOfOrders: MutableCollection<OrderHistory> = userOrders.values

        return HttpResponse.ok(listOfOrders)

    }


}


data class OrderHistory constructor(val type : String, val qty: Int, val price : Int, val createdBy : String, val esop_type: Int) {
    var status = "unfilled"
    var filled = ArrayList<PriceQtyPair>()
    var id: Int = 0
    var timestamp = System.currentTimeMillis()
    var filledQty = 0
}

