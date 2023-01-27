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

        var x=OrderController().orderHandler(user1.userName,"BUY",1,20,"NORMAL")

        //Assert
        Assertions.assertEquals(80,user1.wallet_free)

    }


    @Test
    fun `Check a single sell order performance`() {
        //Arrange
        var user1= User("","","","","kcsp")
        Users[user1.userName]=user1
        user1.inventory_free=40
        user1.perf_free=40
        user1.wallet_free=100

        //Act

        var x=OrderController().orderHandler(user1.userName,"SELL",10,100,"PERFORMANCE")

        //Assert
        Assertions.assertEquals(40,user1.inventory_free)
        Assertions.assertEquals(0,user1.inventory_locked)
        Assertions.assertEquals(100,user1.wallet_free)
        Assertions.assertEquals(0,user1.wallet_locked)
        Assertions.assertEquals(30,user1.perf_free)
        Assertions.assertEquals(10,user1.perf_locked)

    }
}