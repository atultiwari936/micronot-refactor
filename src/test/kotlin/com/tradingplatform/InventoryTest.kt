package com.tradingplatform

import com.tradingplatform.controller.InventoryController
import com.tradingplatform.controller.UserController
import com.tradingplatform.model.User
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class InventoryTest {

    @Test
    fun `Test if user not exist while adding inventory`() {

        val objectOfInventoryController=InventoryController()
        var errorList = arrayListOf<String>()
        val userName="vishal898"

        errorList=objectOfInventoryController.checkIfUserExist(userName)

        Assertions.assertEquals(1,errorList.size)
    }



    @Test
    fun `Test if user exist while adding inventory`() {
        //Arrange
        val user1= User("atul","tiwari","+918888888888","tt@gmail.com","atul_1")
        val objectOfUserController= UserController()
        objectOfUserController.addUser(user1)
        val objectOfInventoryController=InventoryController()
        var errorList = arrayListOf<String>()
        val userName="atul_1"

        errorList=objectOfInventoryController.checkIfUserExist(userName)


        Assertions.assertEquals(0,errorList.size)
    }


}