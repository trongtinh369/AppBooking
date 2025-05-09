package com.example.booktourdemo.models.Tour
data class Schedule(
    var id: String = "",
    var tourId: String = "",
    var open_day: Long = 0L,
    var close_day: Long = 0L,
    var start_date: Long = 0L,
    var end_date: Long = 0L,
    var available_slots: Int = 0,
)