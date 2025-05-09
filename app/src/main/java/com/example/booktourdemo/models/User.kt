package com.example.booktourdemo.models
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val url_avatar: String = "",
    val role: String = "",
    val created_at: Long = 0L
)