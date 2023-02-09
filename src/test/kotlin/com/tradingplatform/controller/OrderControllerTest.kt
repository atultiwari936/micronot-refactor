package com.tradingplatform.controller

import com.tradingplatform.data.OrderRepository
import com.tradingplatform.data.UserRepository
import com.tradingplatform.model.*
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.restassured.specification.RequestSpecification
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@MicronautTest
class OrderControllerTest {

    @BeforeEach
    fun `Remove all the Users and Orders`() {
        OrderRepository.getCompletedOrders().clear()
        OrderRepository.getBuyOrders().clear()
        OrderRepository.getSellOrders().clear()
        UserRepository.users.clear()
    }

    @Test
    fun `check if order request has missing quantity,type and price fields`(spec: RequestSpecification) {
        val user = User("Atul", "Tiwri", "+91999999999", "atul@sahaj.ai", "atul")
        UserRepository.users[user.userName] = user
        spec.`when`()
            .header("Content-Type", "application/json")
            .body("{}")
            .post("/user/${user.userName}/order")
            .then()
            .statusCode(400).and()
            .body(
                "error",
                Matchers.contains(
                    "Enter the quantity field", "Enter the type field", "Enter the price field"
                )
            )
    }

    @Test
    fun `check if order request has missing type and price fields`(spec: RequestSpecification) {
        val user = User("Atul", "Tiwri", "+91999999999", "atul@sahaj.ai", "atul")
        UserRepository.users[user.userName] = user
        spec.`when`()
            .header("Content-Type", "application/json")
            .body("""{"quantity":1}""")
            .post("/user/${user.userName}/order")
            .then()
            .statusCode(400).and()
            .body(
                "error", Matchers.contains(
                    "Enter the type field",
                    "Enter the price field"
                )
            )
    }

    @Test
    fun `check if order request quantity is integer`(spec: RequestSpecification) {
        val user = User("Atul", "Tiwri", "+91999999999", "atul@sahaj.ai", "atul")
        UserRepository.users[user.userName] = user
        spec.`when`()
            .header("Content-Type", "application/json")
            .body(
                """
                {
                    "quantity": "dkfg",
                    "type": "BUY",
                    "price": 20
                }
            """.trimIndent()
            )
            .post("/user/${user.userName}/order")
            .then()
            .statusCode(400).and()
            .body("error", Matchers.contains("Enter a valid quantity"))
    }

    @Test
    fun `check if order request type is string`(spec: RequestSpecification) {
        val user = User("Atul", "Tiwri", "+91999999999", "atul@sahaj.ai", "atul")
        UserRepository.users[user.userName] = user
        spec.`when`()
            .header("Content-Type", "application/json")
            .body(
                """
                {
                    "quantity": 1,
                    "type": 1,
                    "price": 20
                }
            """.trimIndent()
            )
            .post("/user/${user.userName}/order")
            .then()
            .statusCode(400).and()
            .body("error", Matchers.contains("Order Type is not valid"))
    }

    @Test
    fun `Check if error message is returned if order is valid but user not exist `(spec: RequestSpecification) {
        spec.`when`()
            .header("Content-Type", "application/json")
            .body(
                """
                {
                    "quantity": 1,
                    "type": "BUY",
                    "price": 20
                }
            """.trimIndent()
            )
            .post("/user/atul/order")
            .then()
            .statusCode(400).and()
            .body("error", Matchers.contains("User does not exists"))

    }

    @Test
    fun `Check if successful order is placed if order request is valid`(spec: RequestSpecification) {
        val user = User("Atul", "Tiwri", "+91999999999", "atul@sahaj.ai", "atul")
        UserRepository.users[user.userName] = user
        user.wallet.addAmountToFree(100)

        spec.`when`()
            .header("Content-Type", "application/json")
            .body(
                """
                {
                    "quantity": 1,
                    "type": "BUY",
                    "price": 20
                }
            """.trimIndent()
            )
            .post("/user/${user.userName}/order")
            .then()
            .statusCode(200).and()
            .body(
                "orderId", Matchers.equalTo(0),
                "quantity", Matchers.equalTo(1),
                "type", Matchers.equalTo("BUY"),
                "price", Matchers.equalTo(20)
            )
    }


    @Test
    fun `Check if error is returned if free wallet balance is insufficent`(spec: RequestSpecification) {
        val user = User("Atul", "Tiwri", "+91999999999", "atul@sahaj.ai", "atul")
        UserRepository.users[user.userName] = user
        user.wallet.addAmountToFree(10)

        spec.`when`()
            .header("Content-Type", "application/json")
            .body(
                """
                {
                    "quantity": 1,
                    "type": "BUY",
                    "price": 20
                }
            """.trimIndent()
            )
            .post("/user/${user.userName}/order")
            .then()
            .statusCode(400).and()
            .body("error", Matchers.contains("Insufficient funds in wallet"))
    }


    @Test
    fun `Check if error is returned if inventory is insufficent`(spec: RequestSpecification) {
        val user = User("Atul", "Tiwri", "+91999999999", "atul@sahaj.ai", "atul")
        UserRepository.users[user.userName] = user


        spec.`when`()
            .header("Content-Type", "application/json")
            .body(
                """
                {
                    "quantity": 1,
                    "type": "SELL",
                    "price": 20
                }
            """.trimIndent()
            )
            .post("/user/${user.userName}/order")
            .then()
            .statusCode(400).and()
            .body("error", Matchers.contains("Insufficient Normal ESOPs in inventory"))
    }


    @Test
    fun `Check if error is returned if performance inventory is insufficent`(spec: RequestSpecification) {
        val user = User("Atul", "Tiwri", "+91999999999", "atul@sahaj.ai", "atul")
        UserRepository.users[user.userName] = user


        spec.`when`()
            .header("Content-Type", "application/json")
            .body(
                """
                {
                    "quantity": 100,
                    "type": "SELL",
                    "price": 20,
                    "esopType" : "PERFORMANCE"
                }
            """.trimIndent()
            )
            .post("/user/${user.userName}/order")
            .then()
            .statusCode(400).and()
            .body("error", Matchers.contains("Insufficient Performance ESOPs in inventory"))
    }


    @Test
    fun `check if error message is added if price exceed specified limit`(spec: RequestSpecification) {
        val user = User("Atul", "Tiwri", "+91999999999", "atul@sahaj.ai", "atul")
        UserRepository.users[user.userName] = user
        spec.`when`()
            .header("Content-Type", "application/json")
            .body(
                """
                {
                    "quantity": 1,
                    "type": "BUY",
                    "price": 10000000000
                }
            """.trimIndent()
            )
            .post("/user/${user.userName}/order")
            .then()
            .statusCode(400).and()
            .body("error", Matchers.contains("Enter a valid price"))

    }

    @Test
    fun `check if error message is added if quantity exceed specified limit`(spec: RequestSpecification) {
        val user = User("Atul", "Tiwri", "+91999999999", "atul@sahaj.ai", "atul")
        UserRepository.users[user.userName] = user
        spec.`when`()
            .header("Content-Type", "application/json")
            .body(
                """
                {
                    "quantity": 1000000000,
                    "type": "BUY",
                    "price": 20
                }
            """.trimIndent()
            )
            .post("/user/${user.userName}/order")
            .then()
            .statusCode(400).and()
            .body("error", Matchers.contains("Quantity is not valid. Range between 1 and ${PlatformData.MAX_INVENTORY_LIMIT}"))
    }

    @Test
    fun `check if error message is returned when order type is invalid`(spec: RequestSpecification) {
        val user = User("Atul", "Tiwri", "+91999999999", "atul@sahaj.ai", "atul")
        UserRepository.users[user.userName] = user
        spec.`when`()
            .header("Content-Type", "application/json")
            .body(
                """
                {
                    "quantity": 1,
                    "type": "BUYYYY",
                    "price": 20
                }
            """.trimIndent()
            )
            .post("/user/${user.userName}/order")
            .then()
            .statusCode(400).and().body(
                "error",
                Matchers.contains("Order Type is not valid")
            )
    }
}
