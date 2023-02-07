package com.tradingplatform.validations

import com.tradingplatform.data.UserRepo
import com.tradingplatform.model.User

class UserReqValidation {
    companion object {

        fun isUserExists(userName: String) : MutableMap<String,List<String>>?{

            val response = mutableMapOf<String, List<String>>()

            if (UserRepo.getUser(userName) !is User) {
                 response["error"]= listOf("User does not exists")
                return response
            }
            return null
        }
    }
}