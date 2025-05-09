package com.example.booktourdemo.manager.api.dangnhapSDK

interface LoginCallback {
    fun onLoginSuccess(email: String, name: String, avatarUrl: String)
    fun onLoginFailure(errorMessage: String)
}