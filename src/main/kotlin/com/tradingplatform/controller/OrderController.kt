package com.tradingplatform.controller

import OrderValidation
import UserValidation
import com.tradingplatform.model.*
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.json.tree.JsonObject
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.ceil
import kotlin.math.roundToInt

@Controller(value="/user")
class OrderController {
    private var format = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")

    @Get(value = "/{userName}/order")
    fun orderHistory(@QueryValue userName: String): Any? {
        val errorList = arrayListOf<String>()
        val response = mutableMapOf<String, MutableList<String>>()
        val userOrders: HashMap<Int,OrderHistory> = hashMapOf()

        UserValidation().isUserExists(errorList,userName)

        if(errorList.isNotEmpty())
        {
            response["error"]=errorList
            return HttpResponse.badRequest(response)
        }

        val userOrderIds = Users[userName]!!.orders
        for(orderId in userOrderIds){

            if(CompletedOrders.containsKey(orderId)){

                if(!userOrders.contains(orderId.first))
                {
                    val currOrder= CompletedOrders[orderId]
                    val partialOrderHistory = OrderHistory(currOrder!!.type,currOrder.qty,currOrder.price,currOrder.createdBy, currOrder.esopType)
                    partialOrderHistory.id=currOrder.id.first
                    partialOrderHistory.status="filled"
                    partialOrderHistory.timestamp=
                        LocalDateTime.ofInstant(Instant.ofEpochMilli(currOrder.timestamp), ZoneOffset.UTC).format(format).toString()
                    partialOrderHistory.filledQty=currOrder.filledQty
                    partialOrderHistory.filled=currOrder.filled

                    userOrders[partialOrderHistory.id] = partialOrderHistory
                }
                else
                {
                    val currOrder=userOrders[orderId.first]
                    val exisitingOrder= CompletedOrders[orderId]

                    currOrder!!.filledQty+=exisitingOrder!!.filledQty
                    currOrder!!.filled.addAll(exisitingOrder.filled)
                }
            }
        }

        for(order in BuyOrders){
            if(userName == order.createdBy){

                val orderId=order.id



                if(!userOrders.contains(orderId.first)) {
                    val partialOrderHistory = OrderHistory(
                        order!!.type,
                        order.qty,
                        order.price,
                        order.createdBy,
                        order.esopType
                    )
                    partialOrderHistory.id = order.id.first
                    partialOrderHistory.status = "unfilled"
                    partialOrderHistory.timestamp =
                        LocalDateTime.ofInstant(Instant.ofEpochMilli(order.timestamp), ZoneOffset.UTC).format(format)
                            .toString()
                    partialOrderHistory.filledQty = order.filledQty
                    partialOrderHistory.filled = order.filled

                    userOrders[partialOrderHistory.id] = partialOrderHistory
                }
                else
                {
                    val currOrder=userOrders[orderId.first]

                    currOrder!!.filledQty += order!!.filledQty
                    currOrder!!.filled.addAll(order.filled)
                }
            }
        }

        for(order in SellOrders){
            if(userName == order.createdBy){
                val orderId=order.id

                if(!userOrders.contains(orderId.first)) {

                    val partialOrderHistory = OrderHistory(
                        order!!.type,
                        order.qty,
                        order.price,
                        order.createdBy,
                        order.esopType
                    )
                    partialOrderHistory.id = order.id.first
                    partialOrderHistory.status = "unfilled"
                    partialOrderHistory.timestamp =
                        LocalDateTime.ofInstant(Instant.ofEpochMilli(order.timestamp), ZoneOffset.UTC).format(format)
                            .toString()
                    partialOrderHistory.filledQty = order.filledQty
                    partialOrderHistory.filled = order.filled

                    userOrders[partialOrderHistory.id] = partialOrderHistory
                }
                else
                {
                    val currOrder=userOrders[orderId.first]

                    currOrder!!.filledQty += order!!.filledQty
                    currOrder!!.filled.addAll(order.filled)
                }

            }
        }

        val listOfOrders: MutableCollection<OrderHistory> = userOrders.values


        for(individualOrder  in listOfOrders)
        {
            if(individualOrder.qty==individualOrder.filledQty)
                individualOrder.status="filled"
            else if(individualOrder.filledQty==0)
                individualOrder.status="unfilled"
            else
                individualOrder.status="partially filled"


            val transOfIndividualOrder=individualOrder.filled

            val transAtSamePrice : ArrayList<PriceQtyPair> = arrayListOf()
            val transIndexAtPrice : MutableMap<Int,Int> = mutableMapOf()

            for(transPriceAndQty in transOfIndividualOrder)
            {
                if(transIndexAtPrice.contains(transPriceAndQty.price)){
                    transAtSamePrice[transIndexAtPrice[transPriceAndQty.price]!!].quantity+=transPriceAndQty.quantity
                }
                else{
                    transAtSamePrice.add(transPriceAndQty)
                    transIndexAtPrice[transPriceAndQty.price] = transAtSamePrice.size-1
                }
            }

            individualOrder.filled=transAtSamePrice
        }



        return HttpResponse.ok(listOfOrders)

    }



    @Post(value = "/{userName}/order")
    fun createOrder(@Body body: JsonObject, @QueryValue userName:String): Any {
        val response = mutableMapOf<String, Any>()
        val errorList = arrayListOf<String>()
        val fieldLists = arrayListOf("quantity", "type", "price")
        for (field in fieldLists) {
            if (OrderValidation().isFieldExists(field, body)) {
                errorList.add("Enter the $field field")
            }
        }
        if (errorList.isNotEmpty()) {
            response["error"]=errorList
            return HttpResponse.badRequest(response)
        }

        val quantity2 = body["quantity"]
        if (quantity2 ==null || !quantity2.isNumber || ceil(quantity2.doubleValue).roundToInt()!= quantity2.intValue) {
            errorList.add("Quantity is not valid")
        }
        val price2 = body["price"]
        if (price2 ==null || !price2.isNumber || ceil(price2.doubleValue).roundToInt()!= price2.intValue) {
            errorList.add("Price is not valid")

        }
        val type2 = body["type"]
        if (type2 ==null || !type2.isString || (type2.stringValue!="SELL" && type2.stringValue!="BUY")) {
            errorList.add("Order Type is not valid")
        }
        if (errorList.isNotEmpty()) {
            response["error"]=errorList
            return HttpResponse.badRequest(response)
        }

        val quantity = quantity2!!.intValue
        val type = type2!!.stringValue
        val price = price2!!.intValue
        val esopType2 = body["esopType"]
        val esopType = if (esopType2 !== null) esopType2.stringValue else "NORMAL"




        OrderValidation().isValidAmount(errorList, quantity, "quantity")
        OrderValidation().isValidAmount(errorList, price,"price")
        OrderValidation().isValidEsopType(errorList, esopType)


        if (errorList.isNotEmpty())
        {
            response["error"]=errorList
            return HttpResponse.badRequest(response)
        }
            return  HttpResponse.ok(orderHandler(userName,type,quantity,price,esopType))
        }

        fun orderHandler(userName: String,type:String,quantity:Int,price:Int,esopType:String="NORMAL"): Any {
            val errorList = arrayListOf<String>()
            val response = mutableMapOf<String, Any>()
            var newOrder : Order? = null
        if(Users.containsKey(userName)){
            val user = Users[userName]!!
            if(type == "BUY"){
                if(quantity * price > user.wallet_free) errorList.add("Insufficient funds in wallet")
                else if(!OrderValidation().isInventoryWithinLimit(errorList,user,quantity))
                else{
                    user.wallet_free -= quantity * price
                    user.wallet_locked += quantity * price
                    user.pendingCreditEsop += quantity
                    newOrder = Order("BUY", quantity, price, userName, esopNormal)
                    user.orders.add(newOrder.id)

                }
            }
            else if(type == "SELL"){
                if (esopType == "PERFORMANCE") {
                    if (quantity > user.perf_free) {
                        errorList.add("Insufficient Performance ESOPs in inventory")
                    }else if(!OrderValidation().isWalletAmountWithinLimit(errorList,user,price*quantity.toDouble()))
                    else {
                        user.perf_locked += quantity
                        user.perf_free -= quantity
                        user.pendingCreditAmount += quantity*price
                        newOrder = Order("SELL", quantity, price, userName, esopPerformance)
                        user.orders.add(newOrder.id)

                    }
                } else if (esopType == "NORMAL") {
                    if (quantity > user.inventory_free) {
                        errorList.add("Insufficient Normal ESOPs in inventory")
                    }else if(!OrderValidation().isWalletAmountWithinLimit(errorList,user,price*quantity*0.98))
                    else {
                        user.inventory_locked += quantity
                        user.inventory_free -= quantity
                        user.pendingCreditAmount += (quantity*price*0.98).toInt()
                        newOrder = Order("SELL", quantity, price, userName, esopNormal)
                        user.orders.add(newOrder.id)
                    }
                }
            }
            else
                errorList.add("Invalid type given")
        } else  {
            errorList.add("User doesn't exist")
        }

        response["error"] = errorList
        if (errorList.isNotEmpty()) {
            return response
        }

        response["orderId"] = newOrder!!.id.first
        response["quantity"] = quantity
        response["type"] = type
        response["price"] = price

        return response
    }


    
}