package com.example.booktourdemo.firebase.DAO
import com.example.booktourdemo.models.Booking.Payment

interface PaymentsDAO {

    suspend fun themThanhToan(payment: Payment): Boolean

    suspend fun docTatCaThanhToan(): List<Payment>

    suspend fun capNhatThanhToan(payment: Payment): Boolean

    suspend fun xoaThanhToan(id: String): Boolean
}
