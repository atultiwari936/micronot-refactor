package com.tradingplatform

import com.tradingplatform.controller.OrderController
import com.tradingplatform.controller.UserController
import com.tradingplatform.controller.WalletController
import com.tradingplatform.model.*
import io.micronaut.json.tree.JsonObject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Objects

class OrderTest {

    @BeforeEach
    fun `Remove all the Users and Orders`(){
        CompletedOrders.clear()
        BuyOrders.clear()
        SellOrders.clear()
        Users.clear()
    }


    @Test
    fun `Check for User added to userList`(){

        //Arrange
        var user1= User("","","","tt@gmail.com","atul_1")
        val objectOfUserController=UserController()
        objectOfUserController.addUser(user1)

        //Actions
        val userObject: User? = Users[user1.userName]

        //Assert
        Assertions.assertEquals(true, Users.containsKey(user1.userName))
        if (userObject != null) {
            Assertions.assertEquals(true,"tt@gmail.com" in userObject.email)
        }
    }

    @Test
    fun `check whether amount added to wallet`(){

        var user1= User("","","","tt@gmail.com","atul_1")
        val objectOfUserController=UserController()
        objectOfUserController.addUser(user1)
        val objectOfWalletController=WalletController()


        objectOfWalletController.addAmountToWallet(user1.userName,1000)


        Assertions.assertEquals(1000,user1.wallet_free)





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

        //Assert
        Assertions.assertEquals(80,user1.wallet_free)
    }

    @Test
    fun `Check buy order satisfied partially by sell order`() {
        //Arrange
        var user1= User("","","","","atul_1")
        Users[user1.userName]=user1
        user1.wallet_free=100

        var user2= User("","","","","atul_2")
        Users[user2.userName]=user2
        user2.inventory_free=10

        //Act
        var objectOfOrderController=OrderController()
        var buyOrderPlacedByUser1=objectOfOrderController.orderHandler(user1.userName,"BUY",5,20)
        var sellOrderPlacedByUser2=objectOfOrderController.orderHandler(user2.userName,"SELL",2,20)


        //Assert
        Assertions.assertEquals(0,user1.wallet_free)
        Assertions.assertEquals(2,user1.inventory_free)
        Assertions.assertEquals(0,user1.inventory_locked)
        Assertions.assertEquals(60,user1.wallet_locked)

        Assertions.assertEquals(39,user2.wallet_free)
        Assertions.assertEquals(8,user2.inventory_free)
        Assertions.assertEquals(0,user2.inventory_locked)
        Assertions.assertEquals(0,user2.wallet_locked)
    }


    @Test
    fun `Check sell order satisfied partially by buy order`() {
        //Arrange
        var user1= User("","","","","atul_1")
        Users[user1.userName]=user1
        user1.inventory_free=11

        var user2= User("","","","","atul_2")
        Users[user2.userName]=user2
        user2.wallet_free=100


        //Act
        var objectOfOrderController=OrderController()
        var sellOrderPlacedByUser1=objectOfOrderController.orderHandler(user1.userName,"SELL",10,20)
        var buyOrderPlacedByUser2=objectOfOrderController.orderHandler(user2.userName,"BUY",5,20)



        //Assert
        Assertions.assertEquals(98,user1.wallet_free)
        Assertions.assertEquals(1,user1.inventory_free)
        Assertions.assertEquals(5,user1.inventory_locked)
        Assertions.assertEquals(0,user1.wallet_locked)

        Assertions.assertEquals(0,user2.wallet_free)
        Assertions.assertEquals(5,user2.inventory_free)
        Assertions.assertEquals(0,user2.inventory_locked)
        Assertions.assertEquals(0,user2.wallet_locked)
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

    @Test
    fun `Check buy order after a sell order`(){
        //Arrange
        var user1= User("","","","","atul_1")
        Users[user1.userName]=user1
        user1.wallet_free=100
        var user2= User("","","","","atul_2")
        Users[user2.userName]=user2
        user2.inventory_free=10
        var objectOfOrderController=OrderController()
        var sellOrderPlacedByUser2=objectOfOrderController.orderHandler(user2.userName,"SELL",2,20)


        //Act
        var buyOrderPlacedByUser1=objectOfOrderController.orderHandler(user1.userName,"BUY",5,20)


        //Assert
        Assertions.assertEquals(0,user1.wallet_free)
        Assertions.assertEquals(2,user1.inventory_free)
        Assertions.assertEquals(0,user1.inventory_locked)
        Assertions.assertEquals(60,user1.wallet_locked)

        Assertions.assertEquals(39,user2.wallet_free)
        Assertions.assertEquals(8,user2.inventory_free)
        Assertions.assertEquals(0,user2.inventory_locked)
        Assertions.assertEquals(0,user2.wallet_locked)
    }

    @Test
    fun `Check sell order after a buy order`(){
        //Arrange
        var user1= User("","","","","atul_1")
        Users[user1.userName]=user1
        user1.wallet_free=100
        var user2= User("","","","","atul_2")
        Users[user2.userName]=user2
        user2.inventory_free=10
        var objectOfOrderController=OrderController()
        var buyOrderPlacedByUser1=objectOfOrderController.orderHandler(user1.userName,"BUY",5,20)


        //Act
        var sellOrderPlacedByUser2=objectOfOrderController.orderHandler(user2.userName,"SELL",2,20)


        //Assert
        Assertions.assertEquals(0,user1.wallet_free)
        Assertions.assertEquals(2,user1.inventory_free)
        Assertions.assertEquals(0,user1.inventory_locked)
        Assertions.assertEquals(60,user1.wallet_locked)

        Assertions.assertEquals(39,user2.wallet_free)
        Assertions.assertEquals(8,user2.inventory_free)
        Assertions.assertEquals(0,user2.inventory_locked)
        Assertions.assertEquals(0,user2.wallet_locked)
    }
}

