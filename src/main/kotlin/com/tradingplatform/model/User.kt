package com.tradingplatform.model

package ai.sahaj.gurukul.apidesign.model

data class User {
    val firstName: String
    val lastName: String
    val phoneNumber: String
    val email: String
    val userName: String
    val wallet = mapOf<String, Int>()
    val inventory = mapOf<String, Int>()
    val orders = arrayOf<Int>()
    val myList = mutableListOf<Any>()


    fun getAccountInfo(): MutableList<Any> {
        myList.add(firstName)
        myList.add(lastName)
        myList.add(phoneNumber)
        myList.add(email)
        myList.add(userName)
        myList.add(wallet)
        myList.add(inventory)
        myList.add(orders)

        return myList;
    }

}