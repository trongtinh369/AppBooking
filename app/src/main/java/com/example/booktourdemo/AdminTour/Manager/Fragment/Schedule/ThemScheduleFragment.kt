package com.example.booktourdemo.AdminTour.Manager.Fragment.Schedule

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.booktourdemo.R
import com.example.booktourdemo.databinding.ThemScheduleLayoutBinding
import com.example.booktourdemo.DatabaseAPI
import com.example.booktourdemo.firebase.OnDatabaseCallback
import com.example.booktourdemo.models.Tour.Schedule
import com.example.booktourdemo.models.Tour.Tour
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.*

class ThemScheduleFragment : Fragment() {

    private lateinit var binding: ThemScheduleLayoutBinding
    private lateinit var databaseAPI: DatabaseAPI
    private lateinit var tourList: ArrayList<Tour>
    private lateinit var scheduleList: ArrayList<Schedule>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ThemScheduleLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseAPI = DatabaseAPI(requireContext())
        tourList = ArrayList()
        scheduleList = ArrayList()

        setupDatePickers()
        setEvent()
    }

    private fun setEvent() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnLuuThongTin.setOnClickListener {
            saveSchedule()
        }
    }

    override fun onResume() {
        super.onResume()

        databaseAPI.docTatCaTours(tourList, object : OnDatabaseCallback {
            override fun onSuccess(temp: Any) {
                setupSpinnerTour()
            }
            override fun onFailure(e: Exception) {}
        })

        databaseAPI.docTatCaLichTrinhToanCuc(object : OnDatabaseCallback {
            override fun onSuccess(temp: Any) {
                if (temp is List<*>) {
                    scheduleList = ArrayList(temp.filterIsInstance<Schedule>())
                    setupSpinnerSchedule()
                }
            }
            override fun onFailure(e: Exception) {}
        })
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
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.edtOpenDay.setOnClickListener { showDatePicker(binding.edtOpenDay) }
        binding.edtCloseDay.setOnClickListener { showDatePicker(binding.edtCloseDay) }
        binding.edtStartDate.setOnClickListener { showDatePicker(binding.edtStartDate) }
        binding.edtEndDate.setOnClickListener { showDatePicker(binding.edtEndDate) }
    }

    private fun setupSpinnerSchedule() {
        val scheduleIds = scheduleList.map { it.tourId }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, scheduleIds)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTour.adapter = adapter
    }

    private fun setupSpinnerTour() {
        val tourIds = tourList.map { it.id }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tourIds)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTour.adapter = adapter
    }

    private fun saveSchedule() {
        val selectedTourIndex = binding.spinnerTour.selectedItemPosition
        if (selectedTourIndex < 0 || tourList.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng chọn một tour", Toast.LENGTH_SHORT).show()
            return
        }
        val tourId = tourList[selectedTourIndex].id
        val openDay = binding.edtOpenDay.tag as? Long
        val closeDay = binding.edtCloseDay.tag as? Long
        val startDate = binding.edtStartDate.tag as? Long
        val endDate = binding.edtEndDate.tag as? Long
        val availableSlots = binding.edtAvailableSlots.text.toString().toIntOrNull()

        if (openDay == null || closeDay == null || startDate == null || endDate == null) {
            Toast.makeText(requireContext(), "Vui lòng chọn đầy đủ các ngày", Toast.LENGTH_SHORT).show()
            return
        }
        if (availableSlots == null || availableSlots <= 0) {
            Toast.makeText(requireContext(), "Vui lòng nhập số chỗ trống hợp lệ", Toast.LENGTH_SHORT).show()
            return
        }

        val schedule = Schedule(
            id = UUID.randomUUID().toString(),
            tourId = tourId,
            open_day = openDay,
            close_day = closeDay,
            start_date = startDate,
            end_date = endDate,
            available_slots = availableSlots
        )

        databaseAPI.themLichTrinh(tourId, schedule, object : OnDatabaseCallback {
            override fun onSuccess(temp: Any) {
                Toast.makeText(requireContext(), "Lưu lịch trình thành công", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_themScheduleFragment_to_quanLiFragment)
            }
            override fun onFailure(e: Exception) {}
        })
    }
}