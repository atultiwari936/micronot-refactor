package com.tradingplatform

import com.tradingplatform.controller.InventoryController
import com.tradingplatform.controller.OrderController
import com.tradingplatform.controller.UserController
import com.tradingplatform.controller.WalletController
import com.tradingplatform.model.*
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
    fun `Check if user data is valid`(){
        val objectOfUserController=UserController()
        val user1= User("vv","vv","+918888888888","tt@gmail.com","atul_1")


        val errorList=objectOfUserController.checkIfInputDataIsValid(user1)



        Assertions.assertEquals(0,errorList.size)

    }


    



    @Test
    fun `Check for User added to userList`(){

        //Arrange
        val user1= User("","","","tt@gmail.com","atul_1")
        val objectOfUserController=UserController()
        objectOfUserController.addUser(user1)

        //Actions
        val userObject: User? = Users[user1.userName]

        //Assert
        Assertions.assertEquals(true, Users.containsKey(user1.userName))
        Assertions.assertEquals(true,"tt@gmail.com" in userObject!!.email)

    }

    @Test
    fun `check whether amount added to wallet`(){

        val user1= User("","","","tt@gmail.com","atul_1")
        val objectOfUserController=UserController()
        objectOfUserController.addUser(user1)
        val objectOfWalletController=WalletController()


        objectOfWalletController.addAmountToWallet(user1.userName,1000)


        Assertions.assertEquals(1000,user1.walletFree)
    }


    @Test
    fun `check whether Performance esops added to inventory`(){

        val user1= User("","","","tt@gmail.com","atul_1")
        val objectOfUserController=UserController()
        objectOfUserController.addUser(user1)
        val objectOfInventoryController=InventoryController()


        objectOfInventoryController.addESOPStoUserInventory(user1.userName,"PERFORMANCE",100)


        Assertions.assertEquals(100,user1.perfFree)
    }


    @Test
    fun `check whether Normal esops added to inventory`(){

        val user1= User("","","","tt@gmail.com","atul_1")
        val objectOfUserController=UserController()
        objectOfUserController.addUser(user1)
        val objectOfInventoryController=InventoryController()


        objectOfInventoryController.addESOPStoUserInventory(user1.userName,"NORMAL",100)


        Assertions.assertEquals(100,user1.inventoryFree)
    }


    @Test
    fun `Check a single buy order`() {
        //Arrange
        val user1= User("","","","","atul_1")
        Users[user1.userName]=user1
        user1.walletFree=100
        val objectOfOrderController=OrderController()

        //Act


       objectOfOrderController.orderHandler(user1.userName,"BUY",1,20,"NORMAL")

        //Assert
        Assertions.assertEquals(80,user1.walletFree)

        //Assert
        Assertions.assertEquals(80,user1.walletFree)
    }

    @Test
    fun `Check buy order satisfied partially by sell order`() {
        //Arrange
        val user1= User("","","","","atul_1")
        Users[user1.userName]=user1
        user1.walletFree=100

        val user2= User("","","","","atul_2")
        Users[user2.userName]=user2
        user2.inventoryFree=10

        //Act
        val objectOfOrderController=OrderController()
        objectOfOrderController.orderHandler(user1.userName,"BUY",5,20)
        objectOfOrderController.orderHandler(user2.userName,"SELL",2,20)


        //Assert
        Assertions.assertEquals(0,user1.walletFree)
        Assertions.assertEquals(2,user1.inventoryFree)
        Assertions.assertEquals(0,user1.inventoryLocked)
        Assertions.assertEquals(60,user1.walletLocked)

        Assertions.assertEquals(39,user2.walletFree)
        Assertions.assertEquals(8,user2.inventoryFree)
        Assertions.assertEquals(0,user2.inventoryLocked)
        Assertions.assertEquals(0,user2.walletLocked)
    }


    @Test
    fun `Check sell order satisfied partially by buy order`() {
        //Arrange
        val user1= User("","","","","atul_1")
        Users[user1.userName]=user1
        user1.inventoryFree=11

        val user2= User("","","","","atul_2")
        Users[user2.userName]=user2
        user2.walletFree=100


        //Act
        val objectOfOrderController=OrderController()
        objectOfOrderController.orderHandler(user1.userName,"SELL",10,20)
        objectOfOrderController.orderHandler(user2.userName,"BUY",5,20)



        //Assert
        Assertions.assertEquals(98,user1.walletFree)
        Assertions.assertEquals(1,user1.inventoryFree)
        Assertions.assertEquals(5,user1.inventoryLocked)
        Assertions.assertEquals(0,user1.walletLocked)

        Assertions.assertEquals(0,user2.walletFree)
        Assertions.assertEquals(5,user2.inventoryFree)
        Assertions.assertEquals(0,user2.inventoryLocked)
        Assertions.assertEquals(0,user2.walletLocked)
    }


    @Test
    fun `Check a single sell order performance`() {
        //Arrange
        val user1= User("","","","","kcsp")
        Users[user1.userName]=user1
        user1.inventoryFree=40
        user1.perfFree=40
        user1.walletFree=100

        //Act

        OrderController().orderHandler(user1.userName,"SELL",10,100,"PERFORMANCE")

        //Assert
        Assertions.assertEquals(40,user1.inventoryFree)
        Assertions.assertEquals(0,user1.inventoryLocked)
        Assertions.assertEquals(100,user1.walletFree)
        Assertions.assertEquals(0,user1.walletLocked)
        Assertions.assertEquals(30,user1.perfFree)
        Assertions.assertEquals(10,user1.perfLocked)
    }

    @Test
    fun `Check buy order after a sell order`(){
        //Arrange
        val user1= User("","","","","atul_1")
        Users[user1.userName]=user1
        user1.walletFree=100
        val user2= User("","","","","atul_2")
        Users[user2.userName]=user2
        user2.inventoryFree=10
        val objectOfOrderController=OrderController()
        objectOfOrderController.orderHandler(user2.userName,"SELL",2,20)


        //Act
        objectOfOrderController.orderHandler(user1.userName,"BUY",5,20)


        //Assert
        Assertions.assertEquals(0,user1.walletFree)
        Assertions.assertEquals(2,user1.inventoryFree)
        Assertions.assertEquals(0,user1.inventoryLocked)
        Assertions.assertEquals(60,user1.walletLocked)

        Assertions.assertEquals(39,user2.walletFree)
        Assertions.assertEquals(8,user2.inventoryFree)
        Assertions.assertEquals(0,user2.inventoryLocked)
        Assertions.assertEquals(0,user2.walletLocked)
    }

    @Test
    fun `Check sell order after a buy order`(){
        //Arrange
        val user1= User("","","","","atul_1")
        Users[user1.userName]=user1
        user1.walletFree=100
        val user2= User("","","","","atul_2")
        Users[user2.userName]=user2
        user2.inventoryFree=10
        val objectOfOrderController=OrderController()
        objectOfOrderController.orderHandler(user1.userName,"BUY",5,20)


        //Act
       objectOfOrderController.orderHandler(user2.userName,"SELL",2,20)


        //Assert
        Assertions.assertEquals(0,user1.walletFree)
        Assertions.assertEquals(2,user1.inventoryFree)
        Assertions.assertEquals(0,user1.inventoryLocked)
        Assertions.assertEquals(60,user1.walletLocked)

        Assertions.assertEquals(39,user2.walletFree)
        Assertions.assertEquals(8,user2.inventoryFree)
        Assertions.assertEquals(0,user2.inventoryLocked)
        Assertions.assertEquals(0,user2.walletLocked)
    }
}

