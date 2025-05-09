package com.example.booktourdemo.manager.api.dangnhapSDK.models

import com.google.gson.annotations.SerializedName

class FacebookUser(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("email")
    val email: String?
)