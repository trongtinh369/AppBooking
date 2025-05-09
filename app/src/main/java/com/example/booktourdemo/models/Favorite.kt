package com.example.booktourdemo.models

data class Favorite(
    val id: String = "",
    val user_id: String = "",
    val tour_id: String = "",
    val created_at: Long = 0L
)
