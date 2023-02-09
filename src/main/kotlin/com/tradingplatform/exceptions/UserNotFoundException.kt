package com.tradingplatform.exceptions

class UserNotFoundException(val errors: List<String>) : Throwable() {
}
