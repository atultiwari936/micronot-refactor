package com.tradingplatform.exceptions

class InvalidOrderException(val errors: List<String>) : Throwable()
