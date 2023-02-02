package com.tradingplatform.model

import com.tradingplatform.controller.InventoryController
import com.tradingplatform.controller.UserController
import com.tradingplatform.validations.UserValidation
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class UserTest {
    @BeforeEach
    fun `Tear down existing data`() {
        Users.clear()
    }

    @ParameterizedTest
    @CsvSource(
        textBlock = """
        check@sahaj..ai.com
        check@sahaj--ai.com
        check@sahaj.911emergency.com
        check@a123456789a123456789a123456789a123456789a123456789a1234567891233.com
        check@a123456789a123456789a123456789a123456789a123456789a123456789123.a123456789a123456789a123456789a123456789a123456789a123456789123.a123456789a123456789a123456789a123456789a123456789a123456789123.a123456789a123456789a123456789a123456789a123456789a123456789123.g665
        check@sahaj.a
        checksahaj.ai
        checksahajai
        check@jhsd#kjn.com
        check@12sjhd.co.in
        8934"""
    )
    fun `email validation should return proper error message`(email: String) {
        val errorMessages = UserValidation().isEmailValid(email)

        assertTrue {
            errorMessages.contains("Invalid email format")
        }
    }

    @ParameterizedTest
    @CsvSource(
        textBlock = """
        check@sahaj.ai.com
        check@sahaj-ai.com
        check@sahaj.e44mergency.com4
        check@a123456789a123456789a123456789a123456789a123456789a123456789123.com
        check@sahaj.co.in.com.hi89
        check@sahaj.ai.co.in
        check@s23haj.h67
        check@s-o-m-e-t-h-i-n-g.ai
        check@a123456789a123456789a123456789a123456789a123456789a123456789123.a123456789a123456789a123456789a123456789a123456789a123456789123.a123456789a123456789a123456789a123456789a123456789a123456789123.a123456789a123456789a123456789a123456789a123456789a123456789123
        check@sahaj.ai
        checks-ahaj@ai.ai"""
    )
    fun `proper email address should not have any error message`(email: String) {

        val errorMessages = UserValidation().isEmailValid(email)

        assertTrue(errorMessages.isEmpty())
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

        assertTrue {
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

    @Test
    fun `Check if username is unique`() {
        val user = User(
            firstName = "Atul", lastName = "Tiwari", email = "atul@gmail.com", phoneNumber = "+919877678987",
            userName = "atul"
        )
        UserController().addUser(user)
        val errorList = arrayListOf<String>()

        val actualResponse = UserValidation().isUserNameValid(errorList, "atul")

        Assertions.assertEquals(false, actualResponse)
        Assertions.assertEquals(errorList[0], "Username already registered")
    }

    @Test
    fun `Check if phoneNumber is unique`() {
        val user = User(
            firstName = "Atul", lastName = "Tiwari", email = "atul@gmail.com", phoneNumber = "+919877678987",
            userName = "atul"
        )
        UserController().addUser(user)
        val errorList = arrayListOf<String>()

        val actualResponse = UserValidation().isPhoneValid(errorList, "+919877678987")

        Assertions.assertEquals(false, actualResponse)
        Assertions.assertEquals(errorList[0], "phoneNumber already registered")
    }
}