package com.example.booktourdemo.AdminTour.Manager.ChucNangTrangChu

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.booktourdemo.AdminTour.adapter.RVCrudTourAdapter
import com.example.booktourdemo.DatabaseAPI
import com.example.booktourdemo.firebase.OnDatabaseCallback
import com.example.booktourdemo.models.Tour.Tour

class ChucNangTrangChu(private val context: Context, private val databaseAPI: DatabaseAPI) {

    // Ham xoa Tour
    fun xoaTour(tour: Tour, position: Int, danhSachTours: ArrayList<Tour>, adapter: RecyclerView.Adapter<*>) {
        AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert)
            .setTitle("Xác nhận xoá")
            .setMessage("Bạn có chắc chắn muốn xoá tour '${tour.title}' không?")
            .setPositiveButton("Xoá") { dialog, _ ->
                databaseAPI.xoaTour(tour.id, object : OnDatabaseCallback {
                    override fun onSuccess(temp: Any) {
                        Toast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT).show()
                        danhSachTours.removeAt(position)
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
    // Hàm lọc sản phẩm theo tên
    fun filterTours(query: String, danhSachTours: List<Tour>, filteredProductList: ArrayList<Tour>, adapter: RVCrudTourAdapter) {
        filteredProductList.clear()
        if (query.isEmpty()) {
            filteredProductList.addAll(danhSachTours)
        } else {
            filteredProductList.addAll(danhSachTours.filter {
                it.title.contains(query, ignoreCase = true)
            })
        }
        adapter.notifyDataSetChanged()
    }
}