package com.example.booktourdemo.AdminTour.Manager.Fragment.Schedule

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.booktourdemo.R
import com.example.booktourdemo.databinding.SuaScheduleLayoutBinding
import com.example.booktourdemo.DatabaseAPI
import com.example.booktourdemo.firebase.OnDatabaseCallback
import com.example.booktourdemo.models.Tour.Schedule
import com.example.booktourdemo.models.Tour.Tour
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class SuaScheduleFragment : Fragment(R.layout.sua_schedule_layout) {
    private lateinit var binding: SuaScheduleLayoutBinding
    private lateinit var databaseAPI: DatabaseAPI
    private lateinit var schedule: Schedule
    private lateinit var tourList: ArrayList<Tour>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = SuaScheduleLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseAPI = DatabaseAPI(requireContext())
        tourList = ArrayList()

        setEvent()
    }

    override fun onResume() {
        super.onResume()
        // Nhận dữ liệu lịch trình cần sửa từ arguments
        val scheduleId = arguments?.getString("scheduleId")
        val tourId = arguments?.getString("tourId")

        if (scheduleId != null && tourId != null) {
            // Lấy thông tin lịch trình từ database và hiển thị lên UI
            databaseAPI.docLichTrinhTheoID(tourId, scheduleId, object : OnDatabaseCallback {
                override fun onSuccess(temp: Any) {
                    schedule = temp as Schedule
                    displayScheduleInfo(schedule)
                    setupDatePickers()
                }
                override fun onFailure(e: Exception) {}
            })
        }
    }

    private fun setupDatePickers() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        fun showDatePicker(editText: TextInputEditText) {
            val calendar = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d ->
                val selectedDate = Calendar.getInstance().apply { set(y, m, d) }
                val timestamp = selectedDate.timeInMillis
                editText.setText(dateFormat.format(timestamp))
                editText.tag = timestamp
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(
                Calendar.DAY_OF_MONTH)).show()
        }

        binding.edtOpenDay.setOnClickListener { showDatePicker(binding.edtOpenDay) }
        binding.edtCloseDay.setOnClickListener { showDatePicker(binding.edtCloseDay) }
        binding.edtStartDate.setOnClickListener { showDatePicker(binding.edtStartDate) }
        binding.edtEndDate.setOnClickListener { showDatePicker(binding.edtEndDate) }
    }

    private fun setEvent() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnLuuThongTin.setOnClickListener {
            updateSchedule()
        }
    }

    private fun displayScheduleInfo(schedule: Schedule) {
        binding.tvTourID.text = schedule.tourId
        binding.edtOpenDay.setText(formatDate(schedule.open_day))
        binding.edtCloseDay.setText(formatDate(schedule.close_day))
        binding.edtStartDate.setText(formatDate(schedule.start_date))
        binding.edtEndDate.setText(formatDate(schedule.end_date))
        binding.edtAvailableSlots.setText(schedule.available_slots.toString())
    }


    private fun updateSchedule() {
        val tourId = schedule.tourId

        // Nếu người dùng không chọn ngày mới, giữ lại ngày cũ từ schedule
        val openDay = binding.edtOpenDay.tag as? Long ?: schedule.open_day
        val closeDay = binding.edtCloseDay.tag as? Long ?: schedule.close_day
        val startDate = binding.edtStartDate.tag as? Long ?: schedule.start_date
        val endDate = binding.edtEndDate.tag as? Long ?: schedule.end_date

        val availableSlots = binding.edtAvailableSlots.text.toString().toIntOrNull()

        if (availableSlots == null || availableSlots <= 0) {
            Toast.makeText(requireContext(), "Vui lòng nhập số chỗ trống hợp lệ", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedSchedule = Schedule(
            id = schedule.id,  // Giữ nguyên ID
            tourId = tourId,
            open_day = openDay,
            close_day = closeDay,
            start_date = startDate,
            end_date = endDate,
            available_slots = availableSlots
        )

        // Cập nhật vào database
        databaseAPI.capNhatLichTrinh(tourId, schedule.id, updatedSchedule, object : OnDatabaseCallback {
            override fun onSuccess(temp: Any) {
                Toast.makeText(requireContext(), "Sửa thành công!", Toast.LENGTH_SHORT).show()
                val navController = findNavController()
                navController.navigate(R.id.action_suaScheduleFragment_to_trangChuScheduleFragment)
            }
            override fun onFailure(e: Exception) {}
        })
    }

    private fun formatDate(timestamp: Long): String {
        return if (timestamp == 0L) { "N/A" }
        else {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}
