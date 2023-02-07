package com.tradingplatform.validations

import com.tradingplatform.data.UserRepo
import com.tradingplatform.model.User
import io.micronaut.json.tree.JsonObject

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

        fun ifValidFields(req:JsonObject): MutableMap<String,List<String>>? {
            val errorList = arrayListOf<String>()
            val response = mutableMapOf<String, List<String>>()

            val fields = arrayListOf("userName", "firstName", "lastName", "phoneNumber", "email")
            for (field in fields) {
                if (UserValidation().isFieldExists(field, req))
                    errorList.add("Enter the $field field")
                else if (req[field] == null || !req[field]!!.isString)
                    errorList.add("$field data type not in valid format")

            }

            if (errorList.isNotEmpty()) {
                response["error"]=errorList
                return response
            }

            return null
        }
    }
}