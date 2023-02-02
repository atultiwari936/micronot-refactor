package com.tradingplatform.controller

import com.tradingplatform.model.User
import com.tradingplatform.model.Users
import com.tradingplatform.validations.maxLimitForWallet
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.restassured.specification.RequestSpecification
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

@MicronautTest
class WalletControllerTest{

    @Test
    fun `valid amount entered on post request`(spec: RequestSpecification){

        val user1= User("","","","tat@gmail.com","sahaj")
//        UserController().addUser(user1)
        Users[user1.userName] = user1
        spec.`when`()
            .header("Content-Type","application/json")
            .body("{\"amount\": 10}")
            .post("/user/sahaj/wallet")
            .then()
            .statusCode(200).and()
            .body("message",Matchers.comparesEqualTo("10 added to account"))

    }

    @Test
    fun `amount entered is negative`(spec: RequestSpecification){
        val user1= User("","","","tat@gmail.com","pkcs")
        UserController().addUser(user1)
        spec.`when`()
            .header("Content-Type","application/json")
            .body("{\"amount\": -10}")
            .post("/user/pkcs/wallet")
            .then()
            .statusCode(400).and()
            .body("error",Matchers.contains("Enter a positive amount"))


    }

    @Test
    fun `amount entered exceeds maxLimit`(spec: RequestSpecification){
        val user1= User("","","","tat@gmail.com","pkcs")
        UserController().addUser(user1)
        spec.`when`()
            .header("Content-Type","application/json")
            .body("{\"amount\": 10000001}")
            .post("/user/pkcs/wallet")
            .then()
            .statusCode(400).and()
            .body("error",Matchers.contains("Enter amount between 0 to $maxLimitForWallet"))


    }
    @Test
    fun `amount entered is a string`(spec:RequestSpecification){

        val user1= User("","","","tat@gmail.com","pkcs")
        UserController().addUser(user1)
        spec.`when`()
            .header("Content-Type","application/json")
            .body("{\"amount\": \"abc\"}")
            .post("/user/pkcs/wallet")
            .then()
            .statusCode(400).and()
            .body("error",Matchers.contains("Amount data type is invalid"))

    }

    @Test
    fun `amount field not entered`(spec:RequestSpecification){
        val user1= User("","","","tat@gmail.com","pkcs")
        UserController().addUser(user1)
        spec.`when`()
            .header("Content-Type", "application/json")
            .body("{}")
            .post("/user/pkcs/wallet")
            .then()
            .statusCode(400).and()
            .body("error", Matchers.contains("Enter the amount field"))
    }

}