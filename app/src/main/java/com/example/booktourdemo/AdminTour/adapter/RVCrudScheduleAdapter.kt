package com.example.booktourdemo.AdminTour.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.booktourdemo.databinding.ItemScheduleCrudBinding
import com.example.booktourdemo.DatabaseAPI
import com.example.booktourdemo.models.Tour.Schedule
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RVCrudScheduleAdapter(
    val context: Context,
    val list: ArrayList<Schedule>,
    val databaseAPI: DatabaseAPI
) : RecyclerView.Adapter<RVCrudScheduleAdapter.MyViewHolder>() {

    private var onItemClickListener: OnRecyclerviewItemClickListener? = null

    interface OnRecyclerviewItemClickListener {
        fun onBtnXoa(view: View, position: Int)
        fun onBtnSua(view: View, position: Int)
    }

    fun setOnMyItemClickListener(onItemClickListener: OnRecyclerviewItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    inner class MyViewHolder(val binding: ItemScheduleCrudBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(onItemClickListener: OnRecyclerviewItemClickListener?) {
            binding.btnXoa.setOnClickListener {
                onItemClickListener?.onBtnXoa(it, adapterPosition)
            }
            binding.btnSuaThongTin.setOnClickListener {
                onItemClickListener?.onBtnSua(it, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemScheduleCrudBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val schedule = list[position]

        (holder.binding as ItemScheduleCrudBinding).tvTourId.text = "Tour: ${schedule.tourId}"
        (holder.binding as ItemScheduleCrudBinding).tvOpenDay.text = formatDate(schedule.open_day)
        (holder.binding as ItemScheduleCrudBinding).tvCloseDay.text = formatDate(schedule.close_day)
        (holder.binding as ItemScheduleCrudBinding).tvStartDate.text = formatDate(schedule.start_date)
        (holder.binding as ItemScheduleCrudBinding).tvEndDate.text = formatDate(schedule.end_date)
        (holder.binding as ItemScheduleCrudBinding).tvAvailableSlots.text = schedule.available_slots.toString()

        // goi bind de thuc hiện Sửa, Xoa k có sẽ không thực hiện
        holder.bind(onItemClickListener)
    }

    private fun formatDate(timestamp: Long): String {
        return if (timestamp == 0L) { "N/A" }
            else {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                sdf.format(Date(timestamp))
            }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}