package com.tradingplatform.validations

import com.fasterxml.jackson.annotation.JsonTypeInfo.As
import com.tradingplatform.data.UserRepo
import com.tradingplatform.model.BuyOrders
import com.tradingplatform.model.CompletedOrders
import com.tradingplatform.model.SellOrders
import com.tradingplatform.model.User
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserReqValidationTest {

    @BeforeEach
    fun `Remove all the Users`() {
        UserRepo.users.clear()
    }


    @Test
    fun `Test if user exist`() {
        //Arrange
        val user1 = User("atul", "tiwari", "+918888888888", "tt@gmail.com", "atul_1")
        UserRepo.addUser(user1)

        val userName = "atul_1"

        val response = UserReqValidation.isUserExists(userName)
        Assertions.assertEquals(null, response)
    }

    @Test
    fun `Test if user does not exist`() {
        val userName = "atul_1"

        val response = UserReqValidation.isUserExists(userName)

        Assertions.assertNotNull(response)
        Assertions.assertEquals(1, response!!.size)
        Assertions.assertEquals(listOf("User does not exists"), response["error"])

    }


}