package com.tradingplatform.data

import com.tradingplatform.model.ESOPType
import com.tradingplatform.model.Order
import com.tradingplatform.model.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OrderRepositoryTest {
    @BeforeEach
    fun setUp() {
        OrderRepository.getBuyOrders().clear()
        OrderRepository.getSellOrders().clear()
        OrderRepository.getCompletedOrders().clear()
    }

    val buyUser = User(
        firstName = "John", lastName = "Doe", userName = "john", email = "john@gmail.com",
        phoneNumber = "+911234567890"
    )
    val sellUser = User(
        firstName = "Uncle", lastName = "Bob", userName = "bob", email = "bob@gmail.com",
        phoneNumber = "+911234567809"
    )

    @Test
    fun `it should add buy order to buyOrder queue`() {
        val buyOrder = Order(type = "BUY", quantity = 10, price = 10, user = buyUser)
        val expectedBuyOrdersSize = 1

        OrderRepository.addBuyOrder(buyOrder)

        assertEquals(true, OrderRepository.checkIfBuyOrdersExists())
        assertEquals(expectedBuyOrdersSize, OrderRepository.getBuyOrders().size)
    }

    @Test
    fun `it should remove buy order from buyOrder queue`() {
        val buyOrder = Order(type = "BUY", quantity = 10, price = 10, user = buyUser)
        OrderRepository.addBuyOrder(buyOrder)
        val expectedBuyOrdersSize = 0

        OrderRepository.removeBuyOrder(buyOrder)

        assertEquals(false, OrderRepository.checkIfBuyOrdersExists())
        assertEquals(expectedBuyOrdersSize, OrderRepository.getBuyOrders().size)
    }

    @Test
    fun `it should remove sell order from sellOrder queue`() {
        val sellOrder = Order(type = "SELL", quantity = 10, price = 10, user = sellUser)
        OrderRepository.addSellOrder(sellOrder)
        val expectedSellOrdersSize = 0

        OrderRepository.removeSellOrder(sellOrder)

        assertEquals(false, OrderRepository.checkIfSellOrdersExists())
        assertEquals(expectedSellOrdersSize, OrderRepository.getSellOrders().size)
    }

    @Test
    fun `it should add sell order to sellOrder queue`() {
        val sellOrder =
            Order(type = "SELL", quantity = 10, price = 10, user = sellUser, esopType = ESOPType.NORMAL.value)
        val expectedSellOrdersSize = 1

        OrderRepository.addSellOrder(sellOrder)

        assertEquals(true, OrderRepository.checkIfSellOrdersExists())
        assertEquals(expectedSellOrdersSize, OrderRepository.getSellOrders().size)
    }

    @Test
    fun `it should raise exception if order other than BUY or SELL are added`() {
        val order =
            Order(type = "PURCHASE", quantity = 10, price = 10, user = sellUser, esopType = ESOPType.NORMAL.value)

        assertThrows<Exception> { OrderRepository.addOrder(order) }
    }
}
