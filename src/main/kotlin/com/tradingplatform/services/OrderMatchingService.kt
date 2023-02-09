package com.tradingplatform.services

import com.tradingplatform.data.OrderRepository
import com.tradingplatform.model.Order
import com.tradingplatform.model.PlatformData
import com.tradingplatform.model.PriceQuantityPair
import java.math.BigInteger
import kotlin.math.ceil
import kotlin.math.min

class OrderMatchingService {

    fun matchSellOrder(buyOrder: Order) {
        while (OrderRepository.checkIfSellOrdersExists()) {
            val potentialSellOrder = OrderRepository.getSellOrders().poll()
            if (potentialSellOrder.price > buyOrder.price || buyOrder.filledQuantity == buyOrder.quantity) {
                OrderRepository.addOrder(potentialSellOrder)
                break
            } else {
                match(buyOrder, potentialSellOrder)
            }
        }
        updateFilledQuantityAndStatusForOrder(buyOrder)
    }

    fun matchBuyOrder(sellOrder: Order) {
        while (OrderRepository.checkIfBuyOrdersExists()) {
            val potentialBuyOrder = OrderRepository.getBuyOrders().poll()
            if (potentialBuyOrder.price < sellOrder.price || sellOrder.filledQuantity == sellOrder.quantity) {
                OrderRepository.addBuyOrder(potentialBuyOrder)
                break
            } else {
                match(potentialBuyOrder, sellOrder)
            }
        }
        updateFilledQuantityAndStatusForOrder(sellOrder)
    }

    private fun updateFilledQuantityAndStatusForOrder(order: Order) {
        if (order.filledQuantity == order.quantity) {
            order.status = "filled"
            OrderRepository.addCompletedOrder(order)
            OrderRepository.removeOrder(order)
            return
        } else if (order.filledQuantity < order.quantity && order.filledQuantity > 0) {
            order.status = "partially filled"
        }
        OrderRepository.addOrder(order)
    }

    private fun match(buyOrder: Order, sellOrder: Order) {
        val buyer = buyOrder.user
        val seller = sellOrder.user
        val quantity = min(
            buyOrder.quantity - buyOrder.filledQuantity,
            sellOrder.quantity - sellOrder.filledQuantity
        )

        if (quantity == 0) {
            return
        }

        buyOrder.filled.add(PriceQuantityPair(sellOrder.price, quantity))
        buyOrder.filledQuantity += quantity

        buyer.wallet.removeAmountFromLocked(quantity * buyOrder.price)

        buyer.wallet.addAmountToFree(quantity * (buyOrder.price - sellOrder.price))

        buyer.inventory.addNormalESOPToFree(quantity)

        buyer.inventory.removeESOPFromCredit(quantity)

        sellOrder.filled.add(PriceQuantityPair(sellOrder.price, quantity))
        sellOrder.filledQuantity += quantity

        if (sellOrder.id.second == 1) {
            seller.wallet.addAmountToFree(quantity * sellOrder.price)
            seller.inventory.removePerformanceESOPFromLocked(quantity)
        } else {
            val taxAmount: Int = ceil(quantity * sellOrder.price * 0.02).toInt()
            seller.wallet.addAmountToFree(quantity * sellOrder.price - taxAmount)
            PlatformData.feesEarned += BigInteger(taxAmount.toString())
            seller.inventory.removeNormalESOPFromLocked(quantity)
        }
        updateFilledQuantityAndStatusForOrder(buyOrder)
        updateFilledQuantityAndStatusForOrder(sellOrder)
    }
}
