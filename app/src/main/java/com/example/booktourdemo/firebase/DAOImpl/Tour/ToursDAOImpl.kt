package com.example.booktourdemo.firebase.DAOImpl.Tour

import android.util.Log
import com.example.booktourdemo.firebase.DAO.Tour.ToursDAO
import com.example.booktourdemo.models.Tour.Location
import com.example.booktourdemo.models.Tour.Schedule
import com.example.booktourdemo.models.Tour.Tour
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FieldPath
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class ToursDAOImpl(private val db: FirebaseFirestore) : ToursDAO {
    private val collection = db.collection("tours")

    override suspend fun themLichTrinh(tourId: String, schedule: Schedule): Boolean {
        return try {
            collection.document(tourId).collection("schedules").document().set(schedule).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun themTour(tour: Tour): Boolean {
        return try {
            collection.document(tour.id).set(tour).await()
            taoSchedulesRong(tour.id)
            taoReviewRong(tour.id)
            true
        } catch (e: Exception) {
            false
        }
    }

    // Tạo subcollection 'schedules' rỗng cho tour nếu không có lịch trình
    override suspend fun taoReviewRong(tourId: String): Boolean {
        return try {
            collection.document(tourId).collection("reviews")
            true
        } catch (e: Exception) {
            false
        }
    }

    // Tạo subcollection 'schedules' rỗng cho tour nếu không có lịch trình
    override suspend fun taoSchedulesRong(tourId: String): Boolean {
        return try {
            collection.document(tourId).collection("schedules")
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun docTatCaTours(): List<Tour> {
        return try {
            val snapshot = collection.get().await()
            snapshot.toObjects(Tour::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }


    override suspend fun capNhatTour(id: String, updates: Map<String, Any>): Boolean {
        return try {
            collection.document(id).update(updates).await()
            true
        } catch (e: Exception) {
            false
        }
    }


    override suspend fun xoaTour(id: String): Boolean {
        return try {
            collection.document(id).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun docTourTheoID(id: String): Tour? {
        return try {
            val doc = collection.document(id).get().await()
            if (doc.exists()) doc.toObject(Tour::class.java)
            else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun layTopTourNhieuNhat(limit: Int): List<Tour> {
        return try {
            val snapshot = collection
                .orderBy("statistics.booking_count", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            snapshot.toObjects(Tour::class.java)
        } catch (e: Exception) {
            Log.d("test","$e")
            emptyList()
        }
    }

    override suspend fun tangLuotDatTour(tourId: String): Boolean {
        return try {
            val tourRef = collection.document(tourId)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(tourRef)
                val tour = snapshot.toObject(Tour::class.java)
                val currentBookings = tour?.statistics?.booking_count ?: 0
                transaction.update(tourRef, "statistics.booking_count", currentBookings + 1)
            }.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Đọc danh sách Tour theo nhiều ID, đồng thời lấy cả danh sách Schedule tương ứng.
// Sử dụng async để tải song song từng document -> tăng hiệu năng.
    override suspend fun docTourTheoListID(idTours: List<String>): List<Tour> = coroutineScope {
        try {
            // Lấy các document Tour theo ID
            val snapshot = collection
                .whereIn(FieldPath.documentId(), idTours)
                .get()
                .await()

            // Tạo danh sách các tác vụ bất đồng bộ để lấy schedules cho từng tour
            val tasks = snapshot.map { tourDoc ->
                async {
                    try {
                        // Lấy danh sách schedule từ subcollection
                        val schedules = tourDoc.reference
                            .collection("schedules")
                            .get()
                            .await()
                            .toObjects(Schedule::class.java)

                        // Ánh xạ document thành đối tượng Tour và gán schedule
                        tourDoc.toObject(Tour::class.java)?.copy(
                            id = tourDoc.id,
                            schedules = schedules
                        )
                    } catch (e: Exception) {
                        Log.e("ToursDAO", "Lỗi khi lấy schedules: ${e.message}")
                        null
                    }
                }
            }

            // Đợi tất cả các tác vụ hoàn thành và lọc bỏ null
            tasks.awaitAll().filterNotNull()
        } catch (e: Exception) {
            Log.e("ToursDAO", "Lỗi đọc list id: ${e.message}")
            emptyList()
        }
    }



    // Đọc danh sách Tour đang mở đăng ký theo danh sách ID Tour
    override suspend fun docDangKyDangMoTheoListIDTour(idTours: List<String>): List<Tour> = coroutineScope {
        try {
            val now = System.currentTimeMillis()

            // Tạo danh sách các tác vụ bất đồng bộ
            val tasks = idTours.map { idTour ->
                async {
                    try {
                        val tourDoc = collection.document(idTour).get().await()

                        if (tourDoc.exists()) {
                            // Truy vấn các schedule đang mở đăng ký
                            val scheduleSnapshots = tourDoc.reference.collection("schedules")
                                .whereLessThanOrEqualTo("open_day", now)
                                .whereGreaterThanOrEqualTo("close_day", now)
                                .get()
                                .await()

                            if (!scheduleSnapshots.isEmpty) {
                                val schedules = scheduleSnapshots.toObjects(Schedule::class.java)
                                tourDoc.toObject(Tour::class.java)?.copy(
                                    id = tourDoc.id,
                                    schedules = schedules
                                )
                            } else null
                        } else null
                    } catch (e: Exception) {
                        null
                    }
                }
            }

            // Lọc ra các tour hợp lệ
            tasks.awaitAll().filterNotNull()
        } catch (e: Exception) {
            Log.e("ToursDAO", "Lỗi theo list idTour: ${e.message}")
            emptyList()
        }
    }



    // Đọc Tour đang mở đăng ký theo ID cụ thể
    override suspend fun docDangKyDangMoTheoIDTour(idTour: String): List<Tour> {
        return try {
            val now = System.currentTimeMillis()
            val tourList = mutableListOf<Tour>()

            val tourDoc = collection.document(idTour).get().await()

            if (tourDoc.exists()) {
                val scheduleSnapshots = tourDoc.reference.collection("schedules")
                    .whereLessThanOrEqualTo("open_day", now)
                    .whereGreaterThanOrEqualTo("close_day", now)
                    .get()
                    .await()

                if (!scheduleSnapshots.isEmpty) {
                    val schedules = scheduleSnapshots.toObjects(Schedule::class.java)
                    val tour = tourDoc.toObject(Tour::class.java)?.copy(
                        id = tourDoc.id,
                        schedules = schedules
                    )
                    tour?.let { tourList.add(it) }
                }
            }

            tourList
        } catch (e: Exception) {
            Log.e("ToursDAO", "Lỗi khi đọc dữ liệu theo idTour $idTour: $e")
            emptyList()
        }
    }


    // Đọc toàn bộ các Tour đang mở đăng ký
    override suspend fun docDangKyDangMo(): List<Tour> = coroutineScope {
        try {
            val now = System.currentTimeMillis()

            // Lấy tất cả các document Tour
            val tourSnapshots = collection.get().await()

            // Xử lý từng Tour bằng async để tải schedules song song
            val tasks = tourSnapshots.documents.map { tourDoc ->
                async {
                    try {
                        val scheduleSnapshots = tourDoc.reference.collection("schedules")
                            .whereLessThanOrEqualTo("open_day", now)
                            .whereGreaterThanOrEqualTo("close_day", now)
                            .get()
                            .await()

                        if (!scheduleSnapshots.isEmpty) {
                            val schedules = scheduleSnapshots.toObjects(Schedule::class.java)
                            tourDoc.toObject(Tour::class.java)?.copy(
                                id = tourDoc.id,
                                schedules = schedules
                            )
                        } else null
                    } catch (e: Exception) {
                        null
                    }
                }
            }

            tasks.awaitAll().filterNotNull()
        } catch (e: Exception) {
            Log.e("ToursDAO", "Lỗi khi đọc tất cả tours: ${e.message}")
            emptyList()
        }
    }



    override suspend fun capNhatDiaDiem(tourId: String, location: Location): Boolean {
        return try {
            collection.document(tourId).update("location", location).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
