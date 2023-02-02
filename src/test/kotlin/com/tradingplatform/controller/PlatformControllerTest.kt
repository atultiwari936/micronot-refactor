package com.tradingplatform.controller

import com.tradingplatform.model.PlatformData
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.restassured.specification.RequestSpecification
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigInteger

@MicronautTest
class PlatformControllerTest {
    @BeforeEach
    fun setUp() {
        PlatformData.feesEarned = BigInteger("0")
    }

    @Test
    fun `should return correct value of collected platform fees`(spec: RequestSpecification) {
        PlatformData.feesEarned = BigInteger("99")

        spec.`when`()
            .get("/platform/fees")
            .then()
            .statusCode(200).and()
            .body("collectedFees", Matchers.equalTo(99))
    }
}