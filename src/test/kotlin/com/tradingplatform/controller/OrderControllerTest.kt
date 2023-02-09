package com.tradingplatform.controller

import com.tradingplatform.data.OrderRepository
import com.tradingplatform.data.UserRepository
import com.tradingplatform.model.PlatformData
import com.tradingplatform.model.User
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.restassured.specification.RequestSpecification
import org.hamcrest.CoreMatchers.equalTo
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
                "errors",
                Matchers.containsInAnyOrder(
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
                "errors", Matchers.containsInAnyOrder(
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
            .body("errors", Matchers.equalTo("Invalid data types provided"))
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
            .body("errors", Matchers.containsInAnyOrder("Order type can only be BUY or SELL"))
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
            .body("errors", Matchers.containsInAnyOrder("User doesn't exist"))

    }

    @Test
    fun `Check if successful buy order is placed if order request is valid`(spec: RequestSpecification) {
        val user = User("Atul", "Tiwri", "+91999999999", "atul@sahaj.ai", "atul")
        UserRepository.users[user.userName] = user
        user.wallet.addAmountToFree(100)

        spec.`when`()
            .header("Content-Type", "application/json")
            .body(
                """
                {    
                    "type": "BUY",
                    "quantity": 1,
                    "price": 1
                }
                """.trimIndent()
            )
            .post("/user/${user.userName}/order")
            .then()
            .statusCode(200).and()
            .body("type", equalTo("BUY"), "quantity", equalTo(1),
                "price", equalTo(1)
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
            .body("errors", Matchers.containsInAnyOrder("Insufficient funds in wallet"))
    }

    @Test
    fun `Check if successful normal sell order is placed if order request is valid`(spec: RequestSpecification) {
        val user = User("Atul", "Tiwri", "+91999999999", "atul@sahaj.ai", "atul")
        UserRepository.users[user.userName] = user
        user.inventory.addNormalESOPToFree(10)

        spec.`when`()
            .header("Content-Type", "application/json")
            .body(
                """
                {    
                    "type": "SELL",
                    "quantity": 1,
                    "price": 1
                }
                """.trimIndent()
            )
            .post("/user/${user.userName}/order")
            .then()
            .statusCode(200).and()
            .body("type", equalTo("SELL"), "quantity", equalTo(1),
                "price", equalTo(1), "esopType", equalTo("NORMAL")
            )
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
            .body("errors", Matchers.containsInAnyOrder("Insufficient Normal ESOPs in inventory"))
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
            .body("errors", Matchers.containsInAnyOrder("Insufficient Performance ESOPs in inventory"))
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
            .body("errors", Matchers.containsInAnyOrder("Enter price between 0 to 10000000"))

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
            .body("errors", Matchers.containsInAnyOrder("Quantity is not valid. Range between 1 and ${PlatformData.MAX_INVENTORY_LIMIT}"))
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
                "errors",
                Matchers.containsInAnyOrder("Order type can only be BUY or SELL")
            )
    }

    @Test
    fun `Check if successful performance sell order is placed if order request is valid`(spec: RequestSpecification) {
        val user = User("Atul", "Tiwri", "+91999999999", "atul@sahaj.ai", "atul")
        UserRepository.users[user.userName] = user
        user.inventory.addPerformanceESOPToFree(10)

        spec.`when`()
            .header("Content-Type", "application/json")
            .body(
                """
                {    
                    "type": "SELL",
                    "quantity": 1,
                    "price": 1,
                    "esopType": "PERFORMANCE"
                }
                """.trimIndent()
            )
            .post("/user/${user.userName}/order")
            .then()
            .statusCode(200).and()
            .body("type", equalTo("SELL"), "quantity", equalTo(1),
                "price", equalTo(1), "esopType", equalTo("PERFORMANCE")
            )
    }
    @Test
    fun `Check if performance sell order is not placed if order request is not valid`(spec: RequestSpecification) {
        val user = User("Atul", "Tiwri", "+91999999999", "atul@sahaj.ai", "atul")
        UserRepository.users[user.userName] = user

        spec.`when`()
            .header("Content-Type", "application/json")
            .body(
                """
                {    
                    "type": "SELL",
                    "quantity": 1,
                    "price": 1,
                    "esopType": "PERFORMANCE"
                }
                """.trimIndent()
            )
            .post("/user/${user.userName}/order")
            .then()
            .statusCode(400).and()
            .body("errors", Matchers.contains("Insufficient Performance ESOPs in inventory"))

    }
    @Test
    fun `Check if normal sell order is not placed if order request is not valid`(spec: RequestSpecification) {
        val user = User("Atul", "Tiwri", "+91999999999", "atul@sahaj.ai", "atul")
        UserRepository.users[user.userName] = user

        spec.`when`()
            .header("Content-Type", "application/json")
            .body(
                """
                {    
                    "type": "SELL",
                    "quantity": 1,
                    "price": 1,
                    "esopType": "NORMAL"
                }
                """.trimIndent()
            )
            .post("/user/${user.userName}/order")
            .then()
            .statusCode(400).and()
            .body("errors", Matchers.contains("Insufficient Normal ESOPs in inventory"))

    }
}
