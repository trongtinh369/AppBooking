package com.example.booktourdemo.AdminTour.Manager.ChucNangTrangChu

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.booktourdemo.DatabaseAPI
import com.example.booktourdemo.firebase.OnDatabaseCallback
import com.example.booktourdemo.models.Tour.Schedule
import com.example.booktourdemo.AdminTour.adapter.RVCrudScheduleAdapter

class ChucNangSchedule(private val context: Context, private val databaseAPI: DatabaseAPI) {

    // Hàm xoá Schedule
    fun xoaSchedule(schedule: Schedule, position: Int, danhSachSchedules: ArrayList<Schedule>, adapter: RecyclerView.Adapter<*>) {
        AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert)
            .setTitle("Xác nhận xoá")
            .setMessage("Bạn có chắc chắn muốn xoá lịch trình này không?")
            .setPositiveButton("Xoá") { dialog, _ ->
                databaseAPI.xoaLichTrinh(schedule.tourId, schedule.id, object : OnDatabaseCallback {
                    override fun onSuccess(temp: Any) {
                        Toast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT).show()
                        danhSachSchedules.removeAt(position)
                        adapter.notifyItemRemoved(position)
                    }

                    override fun onFailure(e: Exception) {
                        Toast.makeText(context, "Xóa thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                })
                dialog.dismiss()
            }
            .setNegativeButton("Huỷ") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // Hàm lọc Schedule theo TourId
    fun filterSchedule(query: String, danhSachSchedule: List<Schedule>, filteredScheduleList: ArrayList<Schedule>, adapter: RVCrudScheduleAdapter) {
        filteredScheduleList.clear()
        if (query.isEmpty()) {
            filteredScheduleList.addAll(danhSachSchedule)
        } else {
            filteredScheduleList.addAll(danhSachSchedule.filter {
                it.tourId.contains(query, ignoreCase = true)
            })
        }
        adapter.notifyDataSetChanged()
    }
}
