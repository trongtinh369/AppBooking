package com.example.booktourdemo.models.Tour

import com.google.firebase.firestore.Exclude

data class Review(
    var id: String = "",
    var userName: String = "",
    var avatar: String = "",
    var rating: Int = 0,
    var comment: String = "",
    var likeCount: Int = 0,
    var created_at: Long = 0L,

    @get:Exclude
    var Replys: ArrayList<Reply> = ArrayList()
)
