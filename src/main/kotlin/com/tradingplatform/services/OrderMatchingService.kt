package com.tradingplatform.services

import com.tradingplatform.model.*
import java.math.BigInteger
import kotlin.math.ceil
import kotlin.math.min

class OrderMatchingService {

    fun matchSellOrder(order: Order) {
        val user = order.user
        while (SellOrders.isNotEmpty()) {
            val potentialSellOrder = SellOrders.poll()
            if (potentialSellOrder.price > order.price || order.filledQuantity == order.quantity) {
                SellOrders.add(potentialSellOrder)
                break
            } else {
                val potentialSellOrderQty =
                    min(
                        order.quantity - order.filledQuantity,
                        potentialSellOrder.quantity - potentialSellOrder.filledQuantity
                    )

                order.filled.add(PriceQuantityPair(potentialSellOrder.price, potentialSellOrderQty))
                order.filledQuantity += potentialSellOrderQty

                user.wallet.removeAmountFromLocked(potentialSellOrderQty * order.price)
                user.wallet.addAmountToFree(potentialSellOrderQty * (order.price - potentialSellOrder.price))
                user.inventory.addNormalESOPToFree(potentialSellOrderQty)

                user.inventory.removeESOPFromCredit(potentialSellOrderQty)

                potentialSellOrder.filled.add(PriceQuantityPair(potentialSellOrder.price, potentialSellOrderQty))
                potentialSellOrder.filledQuantity += potentialSellOrderQty

                if (potentialSellOrder.id.second == 1) {
                    potentialSellOrder.user.wallet.addAmountToFree(potentialSellOrderQty * potentialSellOrder.price)
                    potentialSellOrder.user.inventory.removePerformanceESOPFromLocked(potentialSellOrderQty)
                } else {

                    val taxAmount: Int = ceil(potentialSellOrderQty * potentialSellOrder.price * 0.02).toInt()

                    potentialSellOrder.user.wallet.addAmountToFree(potentialSellOrderQty * potentialSellOrder.price - taxAmount)
                    PlatformData.feesEarned += BigInteger(taxAmount.toString())
                    potentialSellOrder.user.inventory.removeNormalESOPFromLocked(potentialSellOrderQty)
                }

                if (potentialSellOrder.filledQuantity < potentialSellOrder.quantity && potentialSellOrder.filledQuantity > 0) potentialSellOrder.status =
                    "partially filled"
                SellOrders.add(potentialSellOrder)
                if (potentialSellOrder.filledQuantity == potentialSellOrder.quantity) {
                    potentialSellOrder.status = "filled"
                    SellOrders.remove(potentialSellOrder)


                    CompletedOrders[potentialSellOrder.id] = potentialSellOrder
                }
            }
        }
        if (order.filledQuantity == order.quantity) {
            order.status = "filled"
            CompletedOrders[order.id] = order
        } else {
            if (order.filledQuantity in 1 until order.quantity) order.status = "partially filled"
            BuyOrders.add(order)
        }
    }

    fun matchBuyOrder(order: Order) {
        val user = order.user
        while (BuyOrders.isNotEmpty()) {
            val potentialBuyOrder = BuyOrders.poll()
            if (potentialBuyOrder.price < order.price || order.filledQuantity == order.quantity) {
                BuyOrders.add(potentialBuyOrder)
                break
            } else {
                val potentialBuyOrderQty = min(
                    order.quantity - order.filledQuantity,
                    potentialBuyOrder.quantity - potentialBuyOrder.filledQuantity
                )

                order.filled.add(PriceQuantityPair(order.price, potentialBuyOrderQty))
                order.filledQuantity += potentialBuyOrderQty


                if (order.id.second == 1) {
                    user.inventory.removePerformanceESOPFromLocked(potentialBuyOrderQty)
                    user.wallet.addAmountToFree(potentialBuyOrderQty * order.price)
                    user.wallet.removeAmountFromCredit(potentialBuyOrderQty * order.price)
                } else {

                    val taxAmount: Int = ceil(potentialBuyOrderQty * order.price * 0.02).toInt()

                    user.wallet.addAmountToFree(potentialBuyOrderQty * order.price - taxAmount)
                    user.wallet.removeAmountFromCredit(potentialBuyOrderQty * order.price - taxAmount)
                    PlatformData.feesEarned += BigInteger(taxAmount.toString())
                    user.inventory.removeNormalESOPFromLocked(potentialBuyOrderQty)

                }


                potentialBuyOrder.filled.add(PriceQuantityPair(order.price, potentialBuyOrderQty))
                potentialBuyOrder.filledQuantity += potentialBuyOrderQty
                potentialBuyOrder.user.wallet.removeAmountFromLocked(potentialBuyOrderQty * potentialBuyOrder.price)

                potentialBuyOrder.user.wallet.addAmountToFree(potentialBuyOrderQty * (potentialBuyOrder.price - order.price))
                potentialBuyOrder.user.inventory.addNormalESOPToFree(potentialBuyOrderQty)
                if (potentialBuyOrder.filledQuantity < potentialBuyOrder.quantity && potentialBuyOrder.filledQuantity > 0) potentialBuyOrder.status =
                    "partially filled"
                BuyOrders.add(potentialBuyOrder)
                if (potentialBuyOrder.filledQuantity == potentialBuyOrder.quantity) {
                    potentialBuyOrder.status = "filled"
                    BuyOrders.remove(potentialBuyOrder)
                    CompletedOrders[potentialBuyOrder.id] = potentialBuyOrder
                }
            }
        }
        if (order.filledQuantity == order.quantity) {
            order.status = "filled"
            CompletedOrders[order.id] = order
        } else {
            if (order.filledQuantity in 1 until order.quantity) order.status = "partially filled"
            SellOrders.add(order)
        }
    }

}
