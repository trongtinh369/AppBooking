package com.example.booktourdemo.firebase.DAOImpl.Tour

import android.util.Log
import com.example.booktourdemo.firebase.DAO.Tour.ScheduleDAO
import com.example.booktourdemo.models.Tour.Schedule
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ScheduleDAOImpI(private val db: FirebaseFirestore): ScheduleDAO {

    private val tourCollection = db.collection("tours")

    override suspend fun themLichTrinh(tourId: String, schedule: Schedule): Boolean {
        return try {
            tourCollection.document(tourId).collection("schedules").document(schedule.id).set(schedule).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun docSchedulesTheoIds(idTours: List<String>, idSchedules: List<String>): List<Schedule> {
        return try {
            val allSchedules = mutableListOf<Schedule>()

            // Chia idSchedules thành các batch tối đa 10 phần tử (giới hạn của whereIn)
            val scheduleIdBatches = idSchedules.chunked(10)

            // Lặp qua từng tourId
            for (tourId in idTours) {
                // Lặp qua từng batch idSchedules
                for (scheduleBatch in scheduleIdBatches) {
                    val schedulesSnapshot = tourCollection
                        .document(tourId)
                        .collection("schedules")
                        .whereIn("id", scheduleBatch)
                        .get()
                        .await()

                    // Chuyển đổi schedules và thêm tourId
                    val matchedSchedules = schedulesSnapshot.toObjects(Schedule::class.java).map { schedule ->
                        schedule.copy(tourId = tourId)
                    }
                    allSchedules.addAll(matchedSchedules)
                }
            }

            allSchedules
        } catch (e: Exception) {
            Log.d("test", "Lỗi khi lấy schedules: $e")
            emptyList()
        }
    }

    override suspend fun docTatCaLichTrinh(tourId: String): List<Schedule> {
        return try {
            val snapshot = tourCollection.document(tourId).collection("schedules").get().await()
            snapshot.toObjects(Schedule::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun docTatCaLichTrinhToanCuc(): List<Schedule> {
        val allSchedules = mutableListOf<Schedule>()
        return try {
            val tourSnapshots = tourCollection.get().await()
            for (tourDoc in tourSnapshots.documents) {
                val scheduleSnapshots = tourDoc.reference.collection("schedules").get().await()
                val schedules = scheduleSnapshots.toObjects(Schedule::class.java)
                allSchedules.addAll(schedules)
            }
            allSchedules
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun capNhatLichTrinh(tourId: String, scheduleId: String, schedule: Schedule): Boolean {
        return try {
            tourCollection.document(tourId)
                .collection("schedules")
                .document(scheduleId)
                .set(schedule)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun xoaLichTrinh(tourId: String, scheduleId: String): Boolean {
        return try {
            tourCollection.document(tourId).collection("schedules").document(scheduleId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun docLichTrinhTheoID(tourId: String, scheduleId: String): Schedule? {
        return try {
            val snapshot = tourCollection.document(tourId).collection("schedules").document(scheduleId).get().await()
            snapshot.toObject(Schedule::class.java)
        } catch (e: Exception) {
            Log.d("test", "$e")
            null
        }
    }

    override suspend fun capNhatGiaiDoanDangKy(tourId: String, scheduleId: String): Boolean {
        return try {
            val scheduleRef = tourCollection.document(tourId).collection("schedules").document(scheduleId)
            scheduleRef.update("isRegistering", true).await() // ví dụ cập nhật giai đoạn đăng ký
            true
        } catch (e: Exception) {
            false
        }
    }
}
