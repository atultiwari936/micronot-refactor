package com.tradingplatform.validations

import com.tradingplatform.model.User
import io.micronaut.json.tree.JsonNode
import kotlin.math.ceil
import kotlin.math.roundToInt

class WalletReqValidation {
    companion object {
        fun checkWalletValidations(amount: JsonNode?, user: User): ArrayList<String> {
            val errorList = arrayListOf<String>()

            if (amount == null) {
                errorList.add("Enter the amount field")
                return errorList
            }

            if (!amount.isNumber || (ceil(amount.doubleValue).roundToInt() != amount.intValue)) {
                errorList.add("Amount data type is invalid")
            } else if (OrderValidation().isValidAmount(errorList, amount.intValue))
                OrderValidation().isWalletAmountWithinLimit(errorList, user, amount.intValue)
            return errorList
        }
    }
}
