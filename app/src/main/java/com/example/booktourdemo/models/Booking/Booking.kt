package com.example.booktourdemo.models.Booking
data class Booking(
    var close_day_schedule: Long = 0L,
    var created_at: Long = 0L,
    var end_date_schedule: Long = 0L,
    var id: String = "",
    var active: Boolean = true,
    var num_people: Int = 0,
    var payment: Payment = Payment(),
    var schedule_id: String = "",
    val start_day_schedules: Long = 0L,
    var total_price: Double = 0.0,
    var tour_id: String = "",
    var user_id: String = ""
)