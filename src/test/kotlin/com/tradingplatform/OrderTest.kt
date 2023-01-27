package com.tradingplatform

import com.tradingplatform.controller.OrderController
import com.tradingplatform.model.*
import io.micronaut.json.tree.JsonObject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OrderTest {

    @BeforeEach
    fun `Remove all the Users and Orders`(){
        CompletedOrders.clear()
        BuyOrders.clear()
        SellOrders.clear()
        Users.clear()
    }


    @Test
    fun `Check a single buy order`() {
        //Arrange
        var user1= User("","","","","atul_1")
        Users[user1.userName]=user1
        user1.wallet_free=100



        //Act

        var x=OrderController().orderHandler(user1.userName,"BUY",1,20)




        //Assert

        Assertions.assertEquals(80,user1.wallet_free)







    }

}