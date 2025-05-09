package com.example.booktourdemo.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.booktourdemo.databinding.RvItemChuyendichuathanhtoanLayoutBinding
import com.example.booktourdemo.models.Booking.Booking
import com.example.booktourdemo.models.Tour.Schedule
import com.example.booktourdemo.models.Tour.Tour
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

class RVChuyenDiChuaThanhToan(
    private val context: Context,
    private val tours: ArrayList<Tour>,
    private val bookings: ArrayList<Booking>
): RecyclerView.Adapter<RVChuyenDiChuaThanhToan.MyViewHolder>() {

    private var tourMap: Map<String, Tour> = tours.associateBy { it.id }
    private var onItemClickListener: OnRecyclerviewItemClickListener? = null

    fun setOnMyItemClickListener(onItemClickListener: OnRecyclerviewItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun updateChuyenDi(newTour: List<Tour>, newBooking: List<Booking>) {
        tours.clear()
        tours.addAll(newTour)

        bookings.clear()
        bookings.addAll(newBooking)

        tourMap = newTour.associateBy { it.id }
        notifyDataSetChanged()
    }


    interface OnRecyclerviewItemClickListener {
        fun onItemClick(idTour: String, idSchedule: String)
        fun onPayment(idTour: String, oldBooking: Booking)
        fun onHuy(booking: Booking)
    }

    inner class MyViewHolder(val binding: RvItemChuyendichuathanhtoanLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        var idTour = ""
        var idSchedule = ""
        var booking = Booking()

        init {
            // Xử lý click trên toàn bộ item
            binding.root.setOnClickListener {
                onItemClickListener?.onItemClick(idTour, idSchedule)
            }
            binding.btnPay.setOnClickListener{
                onItemClickListener?.onPayment(idTour, booking)
            }
            binding.btnHuy.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("Xác nhận hủy")
                    .setMessage("Bạn có chắc muốn hủy chuyến đi này không?")
                    .setPositiveButton("Hủy chuyến") { dialog, _ ->
                        onItemClickListener?.onHuy(booking)

                        dialog.dismiss()
                    }
                    .setNegativeButton("Không") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RvItemChuyendichuathanhtoanLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return bookings.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var book = bookings[position]
        val tour = tourMap[book.tour_id]
        holder.idTour = book.tour_id
        holder.idSchedule = book.schedule_id
        holder.booking = book


        var subdes = tour!!.description
        if(tour!!.description.length > 28){
            subdes = tour!!.description.substring(0,27)
        }
        with(holder.binding){
            textTourTitle.text = tour?.title
            textDuration.text = "${calculateDaysBetween(book.start_day_schedules, book.end_date_schedule)} ngày"
            textPeople.text = "${book.num_people} người"
            textPrice.text = "VND ${String.format("%,.0f", book?.payment?.amount)}"
            textLocation.text = tour.location.name
            textCity.text = tour.location.city
            textCountry.text = tour.location.country
            textStartDate.text = "Ngày bắt đầu: ${formatDate(book.start_day_schedules)}"
            textEndDate.text = "Ngày kết thúc: ${formatDate(book.end_date_schedule)}"

            Glide.with(context)
                .load(tour!!.images[0])
                .into(imageTourDetail)
        }
    }
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = Date(timestamp)
        return sdf.format(date)
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