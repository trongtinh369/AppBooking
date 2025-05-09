package com.example.booktourdemo.models.Booking
data class Payment(
    var amount: Double = 0.0,
    var method: String = "",
    var status: Boolean = false
)