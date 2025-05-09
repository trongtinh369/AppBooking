package com.example.booktourdemo.firebase.DAO
import com.example.booktourdemo.models.User

interface UsersDAO {
    suspend fun getUserById(id: String): User? // lấy email làm key luôn

    suspend fun addUser(user: User): Boolean

    suspend fun updateUser(id: String, user: User): Boolean

    suspend fun deleteUser(id: String): Boolean

    suspend fun CheckUser(email: String): String
}

