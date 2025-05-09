package com.example.booktourdemo.models.Tour
data class Reply(
    var id: String = "",
    var userName: String = "",
    var reply_text: String = "",
    var likeCount: Int = 0,
    var avatar: String = "",
    var idReview: String = "",
    var created_at: Long = 0L
)