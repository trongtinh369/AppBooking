package com.example.booktourdemo.models.Tour
data class Location(
    var name: String = "",
    var country: String = "",
    var city: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
)