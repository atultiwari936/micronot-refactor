package com.tradingplatform.comparators

import com.tradingplatform.model.Order

class SellOrdersComparator {
    companion object : Comparator<Order> {
        override fun compare(order1: Order, order2: Order): Int {
            return if (order1.esopType == "PERFORMANCE" && order2.type == "NON_PERFORMANCE") {
                -1
            } else if (order1.esopType != "PERFORMANCE" && order2.type == "PERFORMANCE") {
                1
            } else {
                when {
                    order1.price > order2.price -> 1
                    order1.price < order2.price -> -1
                    else -> {
                        (order1.timestamp - order2.timestamp).toInt()
                    }
                }
            }
        }
    }

}
