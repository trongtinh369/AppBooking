package com.example.booktourdemo.manager.api.dangnhapSDK.models

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
interface FacebookAPI {
    companion object{
        const val BASE_URL = "https://graph.facebook.com/v12.0/"
    }
    @GET("me")
    fun getUserInfo(@Query("access_token") accessToken: String, @Query("fields") fields: String = "id,name,email"): Call<FacebookUser>
}