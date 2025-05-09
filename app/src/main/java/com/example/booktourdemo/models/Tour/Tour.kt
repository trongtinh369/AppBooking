package com.example.booktourdemo.models.Tour

import com.google.firebase.firestore.Exclude

data class Tour(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var location: Location = Location(),
    var price: Double = 0.0,
    var duration: Int = 0,
    var created_at: Long = 0L,
    var images: List<String> = emptyList(),
    @get:Exclude var schedules: List<Schedule> = emptyList(),
    @get:Exclude var review: Review = Review(),
    var statistics: Statistics = Statistics()
)
