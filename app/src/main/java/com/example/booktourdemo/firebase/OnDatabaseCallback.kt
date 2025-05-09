package com.example.booktourdemo.firebase

// Định nghĩa callback để thông báo trạng thái thao tác với cơ sở dữ liệu
interface OnDatabaseCallback {
    fun onSuccess(temp: Any)
    fun onFailure(e: Exception)
}