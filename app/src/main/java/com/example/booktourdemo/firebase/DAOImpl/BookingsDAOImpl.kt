package com.example.booktourdemo.firebase.DAOImpl

import android.util.Log
import com.example.booktourdemo.firebase.DAO.BookingsDAO
import com.example.booktourdemo.models.Booking.Booking
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await


class BookingsDAOImpl(private val db: FirebaseFirestore) : BookingsDAO {

    private val collection = db.collection("bookings")

    override suspend fun themBooking(booking: Booking): Boolean {
        return try {
            // Tạo tài liệu mới với ID tự sinh
            val documentRef = collection.document()
            // Cập nhật ID của booking với ID tự sinh
            val updatedBooking = booking.copy(id = documentRef.id)
            // Lưu booking với ID tự sinh vào Firestore
            documentRef.set(updatedBooking).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun docTatCaBookings(): List<Booking> {
        return try {
            val snapshot = collection
                .orderBy("created_at",  Query.Direction.DESCENDING)
                .get()
                .await()
            snapshot.toObjects(Booking::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }


    override suspend fun layDanhSachBookingTheoUser(userId: String): List<Booking> {
        return try {
            val snapshot = collection
                .whereEqualTo("user_id", userId)
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .await()
            snapshot.toObjects(Booking::class.java)
        } catch (e: Exception) {
            Log.d("test", "Lỗi khi lấy danh sách booking: $e")
            emptyList()
        }
    }

    override suspend fun layBookingTheoId(id: String): Booking? {
        return try {
            val snapshot = collection.document(id).get().await()
            snapshot.toObject(Booking::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun capNhatBooking(booking: Booking): Boolean {
        return try {
            collection.document(booking.id).set(booking).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun xoaBooking(id: String): Boolean {
        return try {
            collection.document(id).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

}
