package com.tradingplatform

import com.tradingplatform.controller.InventoryController
import com.tradingplatform.controller.UserController
import com.tradingplatform.model.User
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class InventoryTest {

    @Test
    fun `check whether Performance esops added to inventory`() {

        val user1 = User("", "", "", "tt@gmail.com", "atul_1")
        val objectOfUserController = UserController()
        objectOfUserController.addUser(user1)
        val objectOfInventoryController = InventoryController()


        objectOfInventoryController.addESOPStoUserInventory(user1.userName, "PERFORMANCE", 100)


        Assertions.assertEquals(100, user1.inventory.esopPerformance.free)
    }


    @Test
    fun `check whether Normal esops added to inventory`() {

        val user1 = User("", "", "", "tt@gmail.com", "atul_1")
        val objectOfUserController = UserController()
        objectOfUserController.addUser(user1)
        val objectOfInventoryController = InventoryController()


        objectOfInventoryController.addESOPStoUserInventory(user1.userName, "NORMAL", 100)


        Assertions.assertEquals(100, user1.inventory.esopNormal.free)
    }


}