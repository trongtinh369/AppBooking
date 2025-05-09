package com.example.booktourdemo.firebase.DAO.Tour
import com.example.booktourdemo.models.Tour.Location
import com.example.booktourdemo.models.Tour.Schedule
import com.example.booktourdemo.models.Tour.Tour

interface ToursDAO {

    suspend fun themLichTrinh(tourId: String, schedule: Schedule): Boolean

    suspend fun taoSchedulesRong(tourId: String): Boolean

    suspend fun taoReviewRong(tourId: String): Boolean

    suspend fun themTour(tour: Tour): Boolean

    suspend fun docTatCaTours(): List<Tour>

    suspend fun capNhatTour(id: String, updates: Map<String, Any>): Boolean

    suspend fun xoaTour(id: String): Boolean

    suspend fun docTourTheoID(id: String): Tour?

    suspend fun layTopTourNhieuNhat(limit: Int): List<Tour>

    suspend fun tangLuotDatTour(tourId: String): Boolean

    suspend fun docDangKyDangMo(): List<Tour>

    suspend fun capNhatDiaDiem(tourId: String, location: Location): Boolean

    suspend fun docDangKyDangMoTheoIDTour(tourId: String): List<Tour>

    suspend fun docDangKyDangMoTheoListIDTour(idTours: List<String>): List<Tour>

    suspend fun docTourTheoListID(idTours: List<String>): List<Tour>
}


