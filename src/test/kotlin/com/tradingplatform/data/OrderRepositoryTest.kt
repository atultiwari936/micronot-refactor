package com.tradingplatform.data

import com.tradingplatform.model.ESOPType
import com.tradingplatform.model.Order
import com.tradingplatform.model.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OrderRepositoryTest {
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
            Order(type = "SELL", quantity = 10, price = 10, user = sellUser, esopType = ESOPType.NORMAL.sortOrder)
        val expectedSellOrdersSize = 1

        OrderRepository.addSellOrder(sellOrder)

        assertEquals(true, OrderRepository.checkIfSellOrdersExists())
        assertEquals(expectedSellOrdersSize, OrderRepository.getSellOrders().size)
    }

    @Test
    fun `it should raise exception if sell order is added to buyOrder queue`() {
        val buyOrder =
            Order(type = "SELL", quantity = 10, price = 10, user = sellUser, esopType = ESOPType.NORMAL.sortOrder)

        assertThrows<Exception> { OrderRepository.addBuyOrder(buyOrder) }

    }

    @Test
    fun `it should raise exception if buy order is added to sellOrder queue`() {
        val sellOrder =
            Order(type = "BUY", quantity = 10, price = 10, user = sellUser, esopType = ESOPType.NORMAL.sortOrder)

        assertThrows<Exception> { OrderRepository.addSellOrder(sellOrder) }

    }
}
