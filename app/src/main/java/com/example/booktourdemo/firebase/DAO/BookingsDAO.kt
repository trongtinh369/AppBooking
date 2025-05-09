package com.example.booktourdemo.firebase.DAO
import com.example.booktourdemo.models.Booking.Booking

interface BookingsDAO {

    suspend fun themBooking(booking: Booking): Boolean

    suspend fun docTatCaBookings(): List<Booking>

    suspend fun capNhatBooking(booking: Booking): Boolean

    suspend fun xoaBooking(id: String): Boolean

    suspend fun layDanhSachBookingTheoUser(idUser: String): List<Booking>

    suspend fun layBookingTheoId(id: String): Booking?
}
