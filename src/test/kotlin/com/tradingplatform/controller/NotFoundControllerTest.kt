package com.tradingplatform.controller

import com.tradingplatform.model.User
import com.tradingplatform.model.Users
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.restassured.specification.RequestSpecification
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

@MicronautTest
class NotFoundControllerTest {

    @Test
    fun `return 404 with proper error message`(spec : RequestSpecification) {
        spec.`when`()
            .get("/a")
            .then()
            .statusCode(404).and()
            .body("error", Matchers.contains("Page not found"))

    }
}