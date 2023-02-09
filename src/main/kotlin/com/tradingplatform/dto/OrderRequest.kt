package com.tradingplatform.dto

import com.fasterxml.jackson.annotation.JsonCreator
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull


@Introspected
class OrderRequest @JsonCreator constructor (
    @field:NotNull(message = "Enter the quantity field")
    @field:NotBlank(message = "Enter the quantity field")
    var type: String? = null,
    @field:NotNull(message = "Enter the quantity field")
    @field:NotBlank(message = "Enter the quantity field")
    var quantity: Int? = null,
    @field:NotNull(message = "Enter the quantity field")
    @field:NotBlank(message = "Enter the quantity field")
    var price: Int? = null,
    var esopType: String? = "NORMAL"
)
