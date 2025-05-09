package com.example.booktourdemo

import android.content.Context
import kotlinx.coroutines.*
import com.example.booktourdemo.firebase.DAO.*
import com.example.booktourdemo.firebase.DAO.Tour.ReviewsDAO
import com.example.booktourdemo.firebase.DAO.Tour.ScheduleDAO
import com.example.booktourdemo.firebase.DAO.Tour.ToursDAO
import com.example.booktourdemo.firebase.DAOImpl.*
import com.example.booktourdemo.firebase.DAOImpl.Tour.ReviewRepliesDAOImpl
import com.example.booktourdemo.firebase.DAOImpl.Tour.ReviewsDAOImpl
import com.example.booktourdemo.firebase.DAOImpl.Tour.ScheduleDAOImpI
import com.example.booktourdemo.firebase.DAOImpl.Tour.ToursDAOImpl
import com.example.booktourdemo.firebase.OnDatabaseCallback
import com.example.booktourdemo.models.Booking.Booking
import com.example.booktourdemo.models.Tour.Reply
import com.example.booktourdemo.models.Tour.Review
import com.example.booktourdemo.models.Tour.Schedule
import com.example.booktourdemo.models.Tour.Tour
import com.example.booktourdemo.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlin.String

class DatabaseAPI(context: Context) {
    private val userDAO: UsersDAO
    private val toursDAO: ToursDAO
    private val scheduleDAO: ScheduleDAO
    private val bookingsDAO: BookingsDAO
    private val review: ReviewsDAO
    private val reply: ReviewRepliesDAO

    init {
        val db = FirebaseFirestore.getInstance() // Sử dụng Firebase Firestore
        userDAO = UsersDAOImpl(db)  // Khởi tạo DAO với Firebase Firestore
        toursDAO = ToursDAOImpl(db)
        scheduleDAO = ScheduleDAOImpI(db)
        bookingsDAO = BookingsDAOImpl(db)
        review = ReviewsDAOImpl(db)
        reply = ReviewRepliesDAOImpl(db)
    }

    // Xử lý bất đồng bộ
    // Tạo một CoroutineScope với Job để quản lý vòng đời của coroutine
    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    // ----------------------ReviewReply
    fun themTraLoiDanhGia(tourId: String, reviewid: String, replyNew: Reply, callback: OnDatabaseCallback){
        coroutineScope.launch {
            try {
                val success = reply.themTraLoiDanhGia(tourId, reviewid, replyNew)
                withContext(Dispatchers.Main){
                    callback.onSuccess(success)
                }
            }
            catch (e: Exception){
                withContext(Dispatchers.Main){
                    callback.onFailure(e)
                }
            }
        }
    }
    fun docTatCaRepliesTheoTourId(tourId: String, list: ArrayList<Reply>, callback: OnDatabaseCallback){
        coroutineScope.launch {
            try {
                val success = reply.docTatCaRepliesTheoTourId(tourId)
                list.addAll(success)
                withContext(Dispatchers.Main){
                    callback.onSuccess("")
                }
            }
            catch (e : Exception){
                withContext(Dispatchers.Main){
                    callback.onFailure(e)
                }
            }
        }
    }


    // --------------------------Reviews
    fun capNhapReview(tourId: String, reviewNew: Review, callback: OnDatabaseCallback){
        coroutineScope.launch {
            try {
                val success = review.capNhatReview(tourId, reviewNew)
                withContext(Dispatchers.Main){
                    callback.onSuccess(success)
                }
            }
            catch (e: Exception){
                withContext(Dispatchers.Main){
                    callback.onFailure(e)
                }
            }
        }
    }
    fun docReviewTheoID(idTour: String, idReview: String, callback: OnDatabaseCallback){
        coroutineScope.launch {
            try {
                val success = review.docReviewTheoId(idTour, idReview)
                withContext(Dispatchers.Main){
                    callback.onSuccess(success!!)
                }
            }
            catch (e: Exception){
                withContext(Dispatchers.Main){
                    callback.onFailure(e)
                }
            }
        }
    }
    fun themReview(idTour: String, reviewNew: Review, callback: OnDatabaseCallback){
        coroutineScope.launch {
            try {
                val success = review.themReview(idTour, reviewNew)
                withContext(Dispatchers.Main){
                    callback.onSuccess(success)
                }
            }
            catch (e: Exception){
                withContext(Dispatchers.Main){
                    callback.onFailure(e)
                }
            }
        }
    }

    fun docTatCaReviewsTheoTourID(idTour: String, list: ArrayList<Review>, callback: OnDatabaseCallback){
        coroutineScope.launch {
            try {
                val success = review.docTatCaReviewsTheoTourID(idTour)
                list.addAll(success)
                withContext(Dispatchers.Main){
                    callback.onSuccess("")
                }
            }
            catch (e : Exception){
                withContext(Dispatchers.Main){
                    callback.onFailure(e)
                }
            }
        }
    }

    // --------------------------Booking
    fun xoaBooking(idBooking: String, callback: OnDatabaseCallback){
        coroutineScope.launch {
            try {
                val success = bookingsDAO.xoaBooking(idBooking)
                withContext(Dispatchers.Main){
                    callback.onSuccess(success)
                }
            } catch(e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(e)
                }
            }
        }
    }
    fun capNhapBooking(booking: Booking, callback: OnDatabaseCallback){
        coroutineScope.launch {
            try {
                val success = bookingsDAO.capNhatBooking(booking)
                withContext(Dispatchers.Main) {
                    callback.onSuccess("")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(e)
                }
            }
        }
    }

    fun themBooking(booking: Booking, callback: OnDatabaseCallback) {
        coroutineScope.launch {
            try {
                val success = bookingsDAO.themBooking(booking)
                withContext(Dispatchers.Main) {
                    callback.onSuccess("")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(e)
                }
            }
        }
    }

    fun layDanhSachBookingTheoUser(userId: String, list: ArrayList<Booking>, callback: OnDatabaseCallback) {
        coroutineScope.launch {
            try {
                val result = bookingsDAO.layDanhSachBookingTheoUser(userId)
                list.addAll(result)
                withContext(Dispatchers.Main) {
                    callback.onSuccess("")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(e)
                }
            }
        }
    }
    // --------------------------- SCHEDULE

    fun themLichTrinh(tourId: String, schedule: Schedule, callback: OnDatabaseCallback) {
        coroutineScope.launch {
            try {
                val result = scheduleDAO.themLichTrinh(tourId, schedule)
                withContext(Dispatchers.Main) {
                    callback.onSuccess(result)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(e)
                }
            }
        }
    }

    fun capNhatLichTrinh(tourId: String, scheduleId: String, schedule: Schedule, callback: OnDatabaseCallback) {
        coroutineScope.launch {
            try {
                val result = scheduleDAO.capNhatLichTrinh(tourId, scheduleId, schedule)
                withContext(Dispatchers.Main) {
                    callback.onSuccess(result)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(e)
                }
            }
        }
    }

    fun xoaLichTrinh(tourId: String, scheduleId: String, callback: OnDatabaseCallback) {
        coroutineScope.launch {
            try {
                val result = scheduleDAO.xoaLichTrinh(tourId, scheduleId)
                withContext(Dispatchers.Main) {
                    callback.onSuccess(result)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(e)
                }
            }
        }
    }

    fun docTatCaLichTrinh(tourId: String, callback: OnDatabaseCallback) {
        coroutineScope.launch {
            try {
                val schedules = scheduleDAO.docTatCaLichTrinh(tourId)
                withContext(Dispatchers.Main) {
                    callback.onSuccess(schedules) // Trả về List<Schedule> trực tiếp
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(e)
                }
            }
        }
    }

    fun docLichTrinhTheoID(tourId: String, scheduleId: String, callback: OnDatabaseCallback) {
        coroutineScope.launch {
            try {
                val schedule = scheduleDAO.docLichTrinhTheoID(tourId, scheduleId)
                withContext(Dispatchers.Main) {
                    if (schedule != null) {
                        callback.onSuccess(schedule)
                    } else {
                        callback.onFailure(Exception("Không tìm thấy lịch trình"))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(e)
                }
            }
        }
    }

    fun docTatCaLichTrinhToanCuc(callback: OnDatabaseCallback) {
        coroutineScope.launch {
            try {
                val schedules = scheduleDAO.docTatCaLichTrinhToanCuc()
                withContext(Dispatchers.Main) {
                    callback.onSuccess(Gson().toJson(schedules))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(e)
                }
            }
        }
    }

    fun capNhatGiaiDoanDangKy(tourId: String, scheduleId: String, callback: OnDatabaseCallback) {
        coroutineScope.launch {
            try {
                val result = scheduleDAO.capNhatGiaiDoanDangKy(tourId, scheduleId)
                withContext(Dispatchers.Main) {
                    callback.onSuccess(result)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(e)
                }
            }
        }
    }


    // ---------------------------------- TOUR
    fun tangLuotDatTour(tourId: String, callback: OnDatabaseCallback) {
        coroutineScope.launch {
            try {
                val success = toursDAO.tangLuotDatTour(tourId)
                withContext(Dispatchers.Main) {
                    callback.onSuccess("$success")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(e)
                }
            }
        }
    }
    fun docToursTheoListID(idTours: List<String>, list: ArrayList<Tour>, callback: OnDatabaseCallback){
        coroutineScope.launch {
            try {
                val tours = toursDAO.docTourTheoListID(idTours)
                list.addAll(tours)
                withContext(Dispatchers.Main) {
                    callback.onSuccess("")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(e)
                }
            }
        }
    }

    fun layTopTourNhieuNhat(limit: Int, list: ArrayList<Tour>, callback: OnDatabaseCallback) {
        coroutineScope.launch {
            try {
                val topTours = toursDAO.layTopTourNhieuNhat(limit)
                list.addAll(topTours)
                withContext(Dispatchers.Main) {
                    callback.onSuccess("")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(e)
                }
            }
        }
    }


    fun docDangKyDangMoTheoListIDTour(idTours: List<String>, list: ArrayList<Tour>, callback: OnDatabaseCallback) {
        coroutineScope.launch {
            try {
                val tours = toursDAO.docDangKyDangMoTheoListIDTour(idTours) // Gọi suspend fun mình vừa viết
                list.addAll(tours)
                withContext(Dispatchers.Main) {
                    callback.onSuccess("")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(e)
                }
            }
        }
    }


    fun docDangKyDangMoTheoIDTour(idTour: String, list: ArrayList<Tour>, callback: OnDatabaseCallback){
        coroutineScope.launch {
            try {
                val tours = toursDAO.docDangKyDangMoTheoIDTour(idTour)
                list.addAll(tours)
                withContext(Dispatchers.Main) {
                    callback.onSuccess("")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(e)
                }
            }
        }
    }

    fun docDangKyDangMo(list: ArrayList<Tour>, callback: OnDatabaseCallback) {
        coroutineScope.launch {
            try {
                val tours = toursDAO.docDangKyDangMo()
                list.addAll(tours)
                withContext(Dispatchers.Main) {
                    callback.onSuccess("")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(e)
                }
            }
        }
    }

    fun docTourTheoID(id: String, callback: OnDatabaseCallback) {
        coroutineScope.launch {
            try {
                val success = toursDAO.docTourTheoID(id)
                success?.let {
                    withContext(Dispatchers.Main) {
                        callback.onSuccess(success)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(e)
                }
            }
        }
    }

    fun themTour(tour: Tour, callback: OnDatabaseCallback) {
        coroutineScope.launch {
            try {
                val success = toursDAO.themTour(tour)
                withContext(Dispatchers.Main) {
                    callback.onSuccess("$success")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(e)
                }
            }
        }
    }

    fun docTatCaTours(list: ArrayList<Tour>, callback: OnDatabaseCallback) {
        coroutineScope.launch {
            try {
                val tours = toursDAO.docTatCaTours()
                list.addAll(tours)
                withContext(Dispatchers.Main) {
                    callback.onSuccess(tours)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(e)
                }
            }
        }
    }

    fun capNhatTour(id: String,  updates: Map<String, Any>, callback: OnDatabaseCallback) {
        coroutineScope.launch {
            try {
                val success = toursDAO.capNhatTour(id,  updates)
                withContext(Dispatchers.Main) {
                    callback.onSuccess("$success")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(e)
                }
            }
        }
    }

    fun xoaTour(id: String, callback: OnDatabaseCallback) {
        coroutineScope.launch {
            try {
                val success = toursDAO.xoaTour(id)
                withContext(Dispatchers.Main) {
                    callback.onSuccess("$success")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(e)
                }
            }
        }
    }

    // -----------------------------USER --------------------------
    fun kiemTraUserDaDangKyChua(email: String, callback: OnDatabaseCallback) {
        coroutineScope.launch {
            try {
                val success = userDAO.CheckUser(email)
                withContext(Dispatchers.Main) {
                    callback.onSuccess(success)
                }
            } catch (e: Exception) {
                callback.onFailure(e)
            }
        }
    }

    fun themUser(user: User, callback: OnDatabaseCallback) {
        coroutineScope.launch {
            try {
                val success = userDAO.addUser(user)
                withContext(Dispatchers.Main) {
                    callback.onSuccess("$success")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(e)
                }
            }
        }
    }

    fun capNhatUser(id: String, user: User, callback: OnDatabaseCallback) {
        coroutineScope.launch {
            try {
                val success = userDAO.updateUser(id, user)
                withContext(Dispatchers.Main) {
                    callback.onSuccess("$success")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(e)
                }
            }
        }
    }

    fun layUserQuaID(id: String, callback: OnDatabaseCallback) {
        coroutineScope.launch {
            try {
                val success = userDAO.getUserById(id)
                success?.let {
                    withContext(Dispatchers.Main) {
                        callback.onSuccess(success)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(e)
                }
            }
        }
    }
}