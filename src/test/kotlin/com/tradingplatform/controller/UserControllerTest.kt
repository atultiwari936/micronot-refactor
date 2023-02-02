package com.tradingplatform.controller

import com.tradingplatform.model.User
import com.tradingplatform.model.Users
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.restassured.specification.RequestSpecification
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@MicronautTest
class UserControllerTest {

    @BeforeEach
    fun setUp() {
        Users.clear()
    }

    @Test
    fun `should register user if given valid data with success message`(spec: RequestSpecification) {
        val registerBody = User(
            firstName = "Atul",
            lastName = "Tiwari",
            email = "atul@gmail.com",
            phoneNumber = "+912345678977",
            userName = "atul_99"
        )

        spec.`when`().header("Content-Type", "application/json").body(registerBody).post("/user/register").then()
            .statusCode(200).and().body("message", Matchers.comparesEqualTo("User registered successfully"))
    }

    @Test
    fun `should return field missing message if field not found in request body`(spec: RequestSpecification) {
        spec.`when`().header("Content-Type", "application/json")
            .body("{\"firstName\":\"Atul\", \"lastName\":\"Tiwari\"}").post("/user/register").then().statusCode(400)
            .and().body(
                "error", Matchers.contains(
                    "Enter the userName field", "Enter the phoneNumber field", "Enter the email field"
                )
            )
    }

    @Test
    fun `should return account information for existing user`(spec: RequestSpecification) {
        //Arrange
        val user = User(
            firstName = "Atul",
            lastName = "Tiwari",
            email = "atul@gmail.com",
            phoneNumber = "+912345678977",
            userName = "atul_99"
        )
        UserController().addUser(user)

        //Assert
        spec.`when`().get("/user/atul_99/accountInformation").then().statusCode(200).and()
            .body("firstName", Matchers.equalTo("Atul")).body("lastName", Matchers.equalTo("Tiwari"))
            .body("email", Matchers.equalTo("atul@gmail.com")).body("phoneNumber", Matchers.equalTo("+912345678977"))
            .body("wallet.free", Matchers.equalTo(0)).body("wallet.locked", Matchers.equalTo(0))
            .body("inventory[0].free", Matchers.equalTo(0)).body("inventory[0].locked", Matchers.equalTo(0))
            .body("inventory[0].type", Matchers.equalTo("NON_PERFORMANCE"))
            .body("inventory[1].free", Matchers.equalTo(0)).body("inventory[1].locked", Matchers.equalTo(0))
            .body("inventory[1].type", Matchers.equalTo("PERFORMANCE"))
    }

    @Test
    fun `should return error if user is not available and account information is accessed for the user`(spec: RequestSpecification) {
        spec.`when`().get("/user/atul_99/accountInformation").then().statusCode(400).and()
            .body("error", Matchers.contains("User does not exists"))
    }

    @Test
    fun `should return invalid email if email doesn't follow RFC`(spec: RequestSpecification) {
        val registerBody = User(
            firstName = "Atul", lastName = "Tiwari", email = "atul", phoneNumber = "+912345678977", userName = "atul_99"
        )
        spec.`when`().header("Content-Type", "application/json").body(registerBody).post("/user/register").then()
            .statusCode(400).and().body("error", Matchers.contains("Invalid email format"))
    }

    @Test
    fun `should return invalid data format if input body field is not in string format`(spec: RequestSpecification) {
        spec.`when`()
            .header("Content-Type", "application/json")
            .body(
                """
                {
                        "firstName": "atu",
                        "lastName": "atu_",
                        "userName": "atul",
                        "email": "atuafl@gmail.com",
                        "phoneNumber": 2123123
                }
            """.trimIndent())
            .post("/user/register")
            .then()
            .statusCode(400).and()
            .body("error", Matchers.contains("phoneNumber data type not in valid format"))
    }
}

