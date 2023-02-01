package com.tradingplatform

import com.tradingplatform.controller.UserController
import com.tradingplatform.model.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OrderTest {


    @BeforeEach
    fun `Remove all the Users and Orders`() {
        CompletedOrders.clear()
        BuyOrders.clear()
        SellOrders.clear()
        Users.clear()
    }

    @Test
    fun `Check if user data is valid`(){
        val objectOfUserController=UserController()
        val user1= User("vv","vv","+918888888888","tt@gmail.com","atul_1")


        val errorList=objectOfUserController.checkIfInputDataIsValid(user1)



        Assertions.assertEquals(0,errorList.size)

    }



}

