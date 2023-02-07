package com.tradingplatform

import com.tradingplatform.controller.UserController
import com.tradingplatform.data.UserRepo
import com.tradingplatform.model.*
import com.tradingplatform.validations.UserReqValidation
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OrderTest {


    @BeforeEach
    fun `Remove all the Users and Orders`() {
        CompletedOrders.clear()
        BuyOrders.clear()
        SellOrders.clear()
        UserRepo.users.clear()
    }




}

