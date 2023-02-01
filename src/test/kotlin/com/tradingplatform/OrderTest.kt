package com.tradingplatform

import com.tradingplatform.controller.InventoryController
import com.tradingplatform.controller.OrderController
import com.tradingplatform.controller.UserController
import com.tradingplatform.controller.WalletController
import com.tradingplatform.model.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OrderTest {


    @BeforeEach
    fun `Remove all the Users and Orders`() {
        CompletedOrders.clear()
        BuyOrders.clear()
        SellOrders.clear()
        Users.clear()
    }





    @Test
    fun `Test if filled quantity is initialized to 0 when order is created `() {


        val objectOfOrder = Order("BUY", 10, 20, "atul")

        Assertions.assertEquals(0, objectOfOrder.filledQty)

    }


}

