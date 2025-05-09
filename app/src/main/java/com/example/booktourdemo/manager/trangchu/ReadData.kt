package com.example.booktourdemo.manager.trangchu


import android.util.Log
import com.example.booktourdemo.adapters.RVSchedulesAdapter
import com.example.booktourdemo.adapters.RVTourAdapter
import com.example.booktourdemo.DatabaseAPI
import com.example.booktourdemo.adapters.RVTopTourBanner
import com.example.booktourdemo.firebase.OnDatabaseCallback
import com.example.booktourdemo.models.Tour.Schedule
import com.example.booktourdemo.models.Tour.Tour


class ReadData(private val databaseAPI: DatabaseAPI) {
    fun ReadTrangChu(listTourGoc: ArrayList<Tour>, listTopToursGoc: ArrayList<Tour>, listScheduleGoc: ArrayList<Schedule>, adapterTopTour: RVTopTourBanner, adapterTour: RVTourAdapter, adapterSche: RVSchedulesAdapter) {
        var arrayTour = ArrayList<Tour>()

        listTourGoc.clear()
        listScheduleGoc.clear()
        databaseAPI.docDangKyDangMo(arrayTour, object : OnDatabaseCallback {
            override fun onSuccess(temp: Any) {

                // cho Tour vào
                adapterTour.updateTours(arrayTour)
                listTourGoc.addAll(arrayTour)
                // cho chuyêến đi vào
                adapterSche.updateSchedules(arrayTour)
                listScheduleGoc.addAll(arrayTour.flatMap { it.schedules })


            }

            override fun onFailure(e: Exception) {}
        })

        var arrayTopTour = ArrayList<Tour>()
        listTopToursGoc.clear()
        databaseAPI.layTopTourNhieuNhat(3, arrayTopTour, object : OnDatabaseCallback {
            override fun onSuccess(temp: Any) {
                listTopToursGoc.addAll(arrayTopTour)
                // cho top Tour vào
                adapterTopTour.updateTours(arrayTopTour)
            }

            override fun onFailure(e: Exception) {
                Log.d("test", "$e")
            }
        })
    }

}