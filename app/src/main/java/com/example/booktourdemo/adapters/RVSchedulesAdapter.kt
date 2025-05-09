package com.example.booktourdemo.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.booktourdemo.databinding.RecyclerviewItemdetailLayoutBinding
import com.example.booktourdemo.models.Tour.Schedule
import com.example.booktourdemo.models.Tour.Tour
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class RVSchedulesAdapter(
    private val context: Context,
    private val tours: ArrayList<Tour>,
    private val listSche: ArrayList<Schedule>,
): RecyclerView.Adapter<RVSchedulesAdapter.MyViewHolder>() {

    private var tourMap: Map<String, Tour> = tours.associateBy { it.id }
    private var onItemClickListener: OnRecyclerviewItemClickListener? = null

    fun setOnMyItemClickListener(onItemClickListener: OnRecyclerviewItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun updateSchedules(newTour: List<Tour>) {
        tours.clear()
        tours.addAll(newTour)

        listSche.clear()
        listSche.addAll(tours.flatMap { it.schedules })

        tourMap = newTour.associateBy { it.id }

        notifyDataSetChanged()
    }


    interface OnRecyclerviewItemClickListener {
        fun onItemClick(idTour: String, idSchedule: String)
    }

    inner class MyViewHolder(val binding: RecyclerviewItemdetailLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        var idTour = ""
        var idSchedule = ""
        init {
            // Xử lý click trên toàn bộ item
            binding.root.setOnClickListener {
                onItemClickListener?.onItemClick(idTour, idSchedule)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RecyclerviewItemdetailLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listSche.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var schedule = listSche[position]
        val tour = tourMap[schedule.tourId]
        holder.idTour = schedule.tourId
        holder.idSchedule = schedule.id


        val binding = holder.binding
        var subdes = tour!!.description
        if(tour!!.description.length > 28){
            subdes = tour!!.description.substring(0,27)
        }
        with(binding){
            textTourTitle.text = tour?.title
            textDescription.text = "$subdes..."
            textDuration.text = "${calculateDaysBetween(schedule.start_date, schedule.end_date)} ngày"
            textMaxPeople.text = "Tối đa ${schedule.available_slots} người"
            textPrice.text = "VND ${String.format("%,.0f", tour?.price)}"



            var imgUrl = tour!!.images[0]
            Log.d("testimage","1: $imgUrl")
            if (tour!!.images.size == 2){
                imgUrl = tour!!.images[1]
            }
            Log.d("testimage","2: $imgUrl")
            Glide.with(context)
                .load(imgUrl)
                .into(imageTourDetail)
        }
    }
    fun calculateDaysBetween(startDateMillis: Long, endDateMillis: Long): Long {
        // Chuyển timestamp (Long) thành LocalDate
        val startDate = Instant.ofEpochMilli(startDateMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val endDate = Instant.ofEpochMilli(endDateMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        // Tính số ngày giữa hai ngày
        return ChronoUnit.DAYS.between(startDate, endDate)
    }

}