package com.tradingplatform.controller

import com.tradingplatform.model.User
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.restassured.specification.RequestSpecification
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

@MicronautTest
class UserControllerTest {
    @Test
    fun `should register user if given valid data with success message`(spec: RequestSpecification) {
        val registerBody = User(firstName = "Atul", lastName = "Tiwari", email = "atul@gmail.com",
            phoneNumber = "+912345678977", userName = "atul_99"
        )

        spec.`when`()
            .header("Content-Type", "application/json")
            .body(registerBody)
            .post("/user/register")
            .then()
            .statusCode(200).and()
            .body("message", Matchers.comparesEqualTo("User registered successfully"))
    }

    @Test
    fun `should return field missing message if field not found in request body`(spec: RequestSpecification) {
        spec.`when`()
            .header("Content-Type", "application/json")
            .body("{\"firstName\":\"Atul\", \"lastName\":\"Tiwari\"}")
            .post("/user/register")
            .then()
            .statusCode(400).and()
            .body("error", Matchers.contains("Enter the userName field", "Enter the phoneNumber field",
                "Enter the email field")
            )
    }
}