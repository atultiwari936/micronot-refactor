package com.tradingplatform

import com.tradingplatform.validations.UserValidation
import com.tradingplatform.controller.InventoryController
import com.tradingplatform.controller.UserController
import com.tradingplatform.model.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class UserTest {
    @BeforeEach
    fun `Tear down existing data`() {
        Users.clear()
    }
    @ParameterizedTest
    @ValueSource(strings = ["check@sahaj..ai.com","check@sahaj--ai.com","check@sahaj.911emergency.com",
        "check@a123456789a123456789a123456789a123456789a123456789a1234567891233.com",
        "check@a123456789a123456789a123456789a123456789a123456789a123456789123.a123456789a123456789a123456789a123456789a123456789a123456789123.a123456789a123456789a123456789a123456789a123456789a123456789123.a123456789a123456789a123456789a123456789a123456789a123456789123.g665",
        "check@sahaj.a",
        "checksahaj.ai",
        "checksahajai",
        " ",
        "",
        "8934"
    ])
    fun `Test should check if the user email is invalid`(email :String)
    {
        val errorList = arrayListOf<String>()

        val actualResponse = UserValidation().isEmailValid(errorList,email )

        Assertions.assertEquals(false, actualResponse)
    }
    @ParameterizedTest
    @ValueSource(strings = ["check@sahaj.ai.com","check@sahaj-ai.com","check@sahaj.e44mergency.com4",
        "check@a123456789a123456789a123456789a123456789a123456789a123456789123.com",
        "check@a123456789a123456789a123456789a123456789a123456789a123456789123.a123456789a123456789a123456789a123456789a123456789a123456789123.a123456789a123456789a123456789a123456789a123456789a123456789123.a123456789a123456789a123456789a123456789a123456789a123456789123",
        "check@sahaj.ai",
        "checks-ahaj@ai.ai"
    ])
    fun `Test should check if the user email is valid`(email :String)
    {
        val errorList = arrayListOf<String>()

        val actualResponse = UserValidation().isEmailValid(errorList,email )

        Assertions.assertEquals(true, actualResponse)
    }
    @Test
    fun `Test should return invalid username if username has unwanted special characters`() {
        val sampleUserName = ".."
        val errorList = arrayListOf<String>()

        val actualResponse = UserValidation().isUserNameValid(errorList, sampleUserName)

        Assertions.assertEquals(false, actualResponse)
    }

    @Test
    fun `Test should return true if username is valid`() {
        val sampleUserName = "atul_99"
        val errorList = arrayListOf<String>()

        val actualResponse = UserValidation().isUserNameValid(errorList, sampleUserName)

        Assertions.assertEquals(true, actualResponse)
    }

    @Test
    fun `Test should return invalid phone number if total digits is greater than 13 including country code in phone number`() {
        val samplePhoneNumber = "+91774678767989"
        val errorList = arrayListOf<String>()

        val actualResponse = UserValidation().isPhoneValid(errorList, samplePhoneNumber)

        Assertions.assertEquals(false, actualResponse)
    }

    @Test
    fun `Test should return invalid phone number if total digits is less than 11 including country code in phone number`() {
        val samplePhoneNumber = "+9123456789"
        val errorList = arrayListOf<String>()

        val actualResponse = UserValidation().isPhoneValid(errorList, samplePhoneNumber)

        Assertions.assertEquals(false, actualResponse)
    }

    @Test
    fun `Test should return valid phone number if total digits is between 10 and 14 including country code in phone number`() {
        val samplePhoneNumber = "+912345678998"
        val errorList = arrayListOf<String>()

        val actualResponse = UserValidation().isPhoneValid(errorList, samplePhoneNumber)

        Assertions.assertEquals(true, actualResponse)
    }

    @Test
    fun `Test should return not a valid name if digits included in name`() {
        val sampleFirstName = "Atul0"
        val errorList = arrayListOf<String>()

        val actualResponse = UserValidation().isNameValid(errorList, sampleFirstName)

        Assertions.assertEquals(false, actualResponse)
    }


    @Test
    fun `Test if user not exist while adding inventory`() {

        val objectOfInventoryController = InventoryController()
        val userName = "vishal898"

        val errorList = objectOfInventoryController.checkIfUserExist(userName)

        Assertions.assertTrue {
            errorList.contains("User does not exists")
        }
    }


    @Test
    fun `Test if user exist while adding inventory`() {
        //Arrange
        val user1 = User("atul", "tiwari", "+918888888888", "tt@gmail.com", "atul_1")
        val objectOfUserController = UserController()
        objectOfUserController.addUser(user1)
        val objectOfInventoryController = InventoryController()
        val userName = "atul_1"

        val errorList = objectOfInventoryController.checkIfUserExist(userName)


        Assertions.assertEquals(0, errorList.size)
    }



}