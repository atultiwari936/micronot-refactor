package com.tradingplatform.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull


@Introspected
class OrderRequest @JsonCreator constructor (
    @field:NotNull(message = "type is required")
    @field:NotBlank(message = "type is required")
    var type: String? = null,
    @field:NotNull(message = "quantity is required")
    @field:NotBlank(message = "quantity is required")
    var quantity: Int? = null,
    @field:NotNull(message = "price is required")
    @field:NotBlank(message = "price is required")
    var price: Int? = null,
    var esopType: String? = null
)
