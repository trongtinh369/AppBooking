package com.example.booktourdemo.models

data class Notification(
    val id: String = "",
    val user_id: String = "",
    val message: String = "",
    val status: String = "",
    val created_at: Long = 0L
)