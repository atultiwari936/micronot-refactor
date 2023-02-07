package com.tradingplatform.validations

import com.tradingplatform.model.PlatformData
import com.tradingplatform.model.User
import io.micronaut.json.tree.JsonNode
import io.micronaut.json.tree.JsonObject
import kotlin.math.ceil
import kotlin.math.roundToInt

class InventoryReqValidation {

    companion object {

        fun validateRequest(body: JsonObject,user: User): MutableMap<String, List<String>>? {
            val response = mutableMapOf<String, List<String>>()
            val errorList = arrayListOf<String>()


            isQuantityNull(body["quantity"])?.let { errorList.add(it) }
            if (errorList.isNotEmpty()) {
                response["error"] = errorList
                return response
            }
            isQuantityValid(body["quantity"])?.let { errorList.add(it) }
            if (errorList.isNotEmpty()) {
                response["error"] = errorList
                return response
            }
            isAmountWithinLimit(body["quantity"]!!.intValue)?.let { errorList.add(it) }
            willQuantityExceedLimit(user,body["quantity"]!!.intValue)?.let { errorList.add(it) }
            isEsopTypeValid(body["type"]!!.stringValue)?.let { errorList.add(it) }

            if (errorList.isNotEmpty()) {
                response["error"] = errorList
                return response
            }
            return null

        }

        fun isQuantityNull(quantity: JsonNode?): String? {
            if (quantity == null)
                return "Quantity is missing"
            return null
        }

        private fun isQuantityValid(quantity: JsonNode?): String? {
            if (!quantity!!.isNumber || ceil(quantity.doubleValue).roundToInt() != quantity.intValue)
                return "Quantity data type is invalid"
            return null
        }

        private fun isAmountWithinLimit(quantity: Int): String? {
            if (quantity <= 0 || quantity > maxLimitForInventory)
                return "Quantity is not valid. Range between 1 and $maxLimitForInventory"
            return null
        }

        private fun willQuantityExceedLimit(user: User, quantity: Int): String? {
            if (!user.inventory.isInventoryWithinLimit(quantity))
                return "Cannot place the order. Wallet amount will exceed ${PlatformData.MAX_INVENTORY_LIMIT}"
            return null
        }

        private fun isEsopTypeValid(esopType: String): String? {
            if (esopType == "PERFORMANCE" || esopType == "NORMAL") {
                return null
            }
            return "ESOP type is not valid (Allowed : PERFORMANCE and NON-PERFORMANCE)"
        }

    }
}
