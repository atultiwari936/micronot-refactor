package com.tradingplatform.dto

open class OrderResponse (val type: String, val quantity: Int, val price: Int){
}


class SellOrderResponse(type: String,quantity: Int,price: Int, val esopType: String) : OrderResponse(type = type,quantity=quantity, price=price) {

}
