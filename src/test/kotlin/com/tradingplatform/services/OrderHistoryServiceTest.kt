package com.tradingplatform.services

import com.tradingplatform.data.OrderRepository
import com.tradingplatform.model.Order
import com.tradingplatform.model.User
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class OrderHistoryServiceTest {

    val user = User(firstName = "John", lastName = "Doe", userName = "john",
        phoneNumber = "+917878787878", email = "john@gmail.com"
    )

    @Test
    fun `Test if return empty list if no orders placed by user`() {
        val expectedOrdersList = mutableListOf<Order>()

        val actualOrders = OrderHistoryService.getAllOrders(user)

        Assertions.assertEquals(expectedOrdersList, actualOrders)
    }

    @Test
    fun `it should return all the orders of the user given orders are present`() {
        val order = Order(type = "BUY", quantity = 1, price = 1, user)
        OrderRepository.addOrder(order)
        val expectedOrdersList = mutableListOf<Order>()
        expectedOrdersList.add(order)

        val actualOrders = OrderHistoryService.getAllOrders(user)

        Assertions.assertEquals(expectedOrdersList, actualOrders)
    }


}