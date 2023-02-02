package com.tradingplatform.model

data class User constructor(val firstName : String, val lastName : String, val phoneNumber : String, val email: String, val userName: String){
    var walletFree = 0
    var walletLocked = 0
    var inventoryFree = 0
    var inventoryLocked = 0
    var perfFree = 0
    var perfLocked = 0
    var pendingCreditAmount = 0
    var pendingCreditEsop = 0
    val orders = arrayListOf<Pair<Int,Int>>()
}

val Users = HashMap<String,User>()
