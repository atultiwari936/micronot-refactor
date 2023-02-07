package com.tradingplatform.validations

import io.micronaut.json.tree.JsonNode
import io.micronaut.json.tree.JsonObject
import kotlin.math.ceil
import kotlin.math.roundToInt

class OrderReqValidation {
    companion object {


        fun validateRequest(body: JsonObject): MutableMap<String, List<String>>? {
            var response = isFieldsPresent(body)
            if (response != null)
                return response
            response = isFieldsValid(body)
            if (response != null)
                return response
            return null

        }

        private fun isFieldsPresent(body: JsonObject): MutableMap<String, List<String>>? {

            val response = mutableMapOf<String, List<String>>()

            val errorList = arrayListOf<String>()

            val fieldLists = arrayListOf("quantity", "type", "price")

            for (field in fieldLists) {
                if (body[field] == null)
                    errorList.add("Enter the $field field")
            }
            if (errorList.isNotEmpty()) {
                response["error"] = errorList
                return response
            }
            return null
        }

        private fun isFieldsValid(body: JsonObject): MutableMap<String, List<String>>? {
            val response = mutableMapOf<String, List<String>>()
            val errorList = arrayListOf<String>()
            isQuantityValid(body["quantity"])?.let { errorList.add(it) }
            isPriceValid(body["price"])?.let { errorList.add(it) }
            isTypeValid(body["type"])?.let { errorList.add(it) }

            if (errorList.isNotEmpty()) {
                response["error"] = errorList
                return response
            }
            return null
        }

        private fun isQuantityValid(quantity: JsonNode?): String? {
            if (quantity == null || !quantity.isNumber || ceil(quantity.doubleValue).roundToInt() != quantity.intValue) {
                return "Enter a valid quantity"
            }
            return null
        }

        private fun isPriceValid(price: JsonNode?): String? {
            if (price == null || !price.isNumber || ceil(price.doubleValue).roundToInt() != price.intValue) {
                return "Enter a valid price"
            }
            return null
        }

        private fun isTypeValid(type: JsonNode?): String? {
            if (type == null || !type.isString || (type.stringValue != "SELL" && type.stringValue != "BUY")) {
                return "Order Type is not valid"
            }
            return null
        }

        fun isValueValid(quantity: Int, price: Int, esopType: String): MutableMap<String, List<String>>? {
            val response = mutableMapOf<String, List<String>>()
            val errorList = arrayListOf<String>()

            isQuantityWithinLimit(quantity)?.let { errorList.add(it) }
            isPriceWithinLimit(price)?.let { errorList.add(it) }
            isEsopTypeValid(esopType)?.let { errorList.add(it) }
            if (errorList.isNotEmpty()) {
                response["error"] = errorList
                return response
            }
            return null
        }

        private fun isQuantityWithinLimit(quantity: Int): String? {
            if (quantity <= 0 || quantity > maxLimitForInventory) {
                return "Quantity is not valid. Range between 1 and $maxLimitForInventory"
            }
            return null
        }


        private fun isPriceWithinLimit(price: Int): String? {
            if (price <= 0) {
                return "Enter a positive price"
            } else if (price > maxLimitForWallet) {
                return "Enter price between 0 to $maxLimitForWallet"
            }
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