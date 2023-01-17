package com.tradingplatform.model

data class User constructor(val firstName : String, val lastName : String, val phoneNumber : String, val email: String, val userName: String){
    var wallet_free = 0
    var wallet_locked = 0
    var inventory_free = 0
    var inventory_locked = 0
    val orders = arrayOf<Int>()
//    fun getAccountInfo(): MutableList<Any> {
//        myList.add(firstName)
//        myList.add(lastName)
//        myList.add(phoneNumber)
//        myList.add(email)
//        myList.add(userName)
//        myList.add(wallet)
//        myList.add(inventory)
//        myList.add(orders)
//
//        return myList;
//    }

}

val Users = HashMap<Int,User>()