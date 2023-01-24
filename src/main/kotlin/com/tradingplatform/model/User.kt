package com.tradingplatform.model

data class User constructor(val firstName : String, val lastName : String, val phoneNumber : String, val email: String, val userName: String){
    var wallet_free = 0
    var wallet_locked = 0
    var inventory_free = 0
    var inventory_locked = 0
    var perf_free = 0
    var perf_locked = 0
    val orders = arrayListOf<Pair<Int,Int>>()
}

val Users = HashMap<String,User>()

data class Register(val firstName: String,
                    val lastName: String,
                    val email: String,
                    val userName: String,
                    val phoneNumber: String)