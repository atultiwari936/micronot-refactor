package com.tradingplatform.validations

import com.tradingplatform.dto.OrderRequest
import com.tradingplatform.model.PlatformData
import com.tradingplatform.model.User
import com.tradingplatform.model.Wallet


class OrderValidation {
    companion object {

        fun validateOrder(order: OrderRequest): MutableMap<String, List<String>>? {
            val response = mutableMapOf<String, List<String>>()
            val errorList = arrayListOf<String>()

            isQuantityWithinLimit(order.quantity!!)?.let { errorList.add(it) }
            isPriceWithinLimit(order.price!!)?.let { errorList.add(it) }
            isEsopTypeValid(order.esopType!!)?.let { errorList.add(it) }

            if (errorList.isNotEmpty()) {
                response["error"] = errorList
                return response
            }
            return null
        }

        private fun isQuantityWithinLimit(quantity: Int): String? {
            if (quantity <= 0 || quantity > PlatformData.MAX_INVENTORY_LIMIT) {
                return "Quantity is not valid. Range between 1 and ${PlatformData.MAX_INVENTORY_LIMIT}"
            }
            return null
        }


        private fun isPriceWithinLimit(price: Int): String? {
            if (price <= 0) {
                return "Enter a positive price"
            } else if (price > Wallet.MAX_WALLET_LIMIT) {
                return "Enter price between 0 to ${Wallet.MAX_WALLET_LIMIT}"
            }
            return null
        }

        private fun isEsopTypeValid(esopType: String): String? {
            if (esopType == "PERFORMANCE" || esopType == "NORMAL") {
                return null
            }
            return "ESOP type is not valid (Allowed : PERFORMANCE and NON-PERFORMANCE)"
        }

        fun validateBuyOrder(order: OrderRequest, user: User): MutableList<String> {
            val quantity = order.quantity!!
            val price = order.price!!
            val totalAmount = quantity * price
            val errorList = mutableListOf<String>()

            if (totalAmount > user.wallet.getFreeAmount()) {
                errorList.add("Insufficient funds in wallet")
            } else if (!user.inventory.isInventoryWithinLimit(quantity)) {
                errorList.add("ESOPs quantity will exceed maximum limit of ${PlatformData.MAX_INVENTORY_LIMIT}")
            }
            return errorList
        }
    }

    fun isValidAmount(list: ArrayList<String>, amount: Int): Boolean {
        if (amount <= 0) {
            list.add("Enter a positive amount")
            return false
        } else if (amount > Wallet.MAX_WALLET_LIMIT) {

            list.add("Enter amount between 0 to ${Wallet.MAX_WALLET_LIMIT}")
            return false
        }
        return true

    }

    fun isWalletAmountWithinLimit(list: ArrayList<String>, user: User, amount: Int): Boolean {
        if (!user.wallet.isWalletAmountWithinLimit(amount)) {
            list.add("Cannot place the order. Wallet amount will exceed ${Wallet.MAX_WALLET_LIMIT}")
            return false
        }
        return true
    }

    fun isSufficientPerformanceEsopsQuantity(errorList: ArrayList<String>, user: User, quantity: Int) {
        if (quantity > user.inventory.getPerformanceFreeQuantity()) {
            errorList.add("Insufficient Performance ESOPs in inventory")
        }
    }

    fun isSufficientNonPerformanceEsopsQuantity(errorList: ArrayList<String>, user: User, quantity: Int) {
        if (quantity > user.inventory.getNormalFreeQuantity()) {
            errorList.add("Insufficient Performance ESOPs in inventory")
        }
    }
}
