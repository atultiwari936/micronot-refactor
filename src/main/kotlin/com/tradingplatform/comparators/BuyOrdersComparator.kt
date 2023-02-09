package com.tradingplatform.comparators

import com.tradingplatform.model.Order

class BuyOrdersComparator {
    companion object : Comparator<Order> {
        override fun compare(order1: Order, order2: Order): Int {
            return when {
                order1.price > order2.price -> -1
                order1.price < order2.price -> 1
                else -> {
                    (order1.timestamp - order2.timestamp).toInt()
                }
            }
        }
    }
}
