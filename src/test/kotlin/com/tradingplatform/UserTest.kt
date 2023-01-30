package com.tradingplatform

import UserValidation
import com.tradingplatform.model.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserTest {
    @BeforeEach
    fun `Tear down existing data`() {
        Users.clear()
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
}