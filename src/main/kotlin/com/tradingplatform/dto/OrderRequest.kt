package com.tradingplatform.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull


@Introspected
class OrderRequest @JsonCreator constructor(
    @field:NotNull(message = "Enter the type field")
    var type: String? = null,

    @field:NotNull(message = "Enter the quantity field")
    @field:Type(Integer::class)
    @field:Min(1, message = "Order quantity should be greater than 0")
    var quantity: Int? = null,

    @field:NotNull(message = "Enter the price field")
    @field:Min(1, message = "Order price should be greater than 0")
    var price: Int? = null,

    var esopType: String? = "NORMAL"
)
