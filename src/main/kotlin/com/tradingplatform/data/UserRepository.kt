package com.tradingplatform.data

import com.tradingplatform.model.User

object UserRepository {
    var users = HashMap<String, User>()

    fun addUser(user: User) {
        users[user.userName] = user
    }

    fun getUser(userName: String): User? {
        return users[userName]
    }
}
