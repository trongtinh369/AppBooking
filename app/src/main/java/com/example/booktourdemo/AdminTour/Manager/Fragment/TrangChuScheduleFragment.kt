package com.example.booktourdemo.AdminTour.Manager.Fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booktourdemo.AdminTour.Manager.ChucNangTrangChu.ChucNangSchedule
import com.example.booktourdemo.AdminTour.adapter.RVCrudScheduleAdapter
import com.example.booktourdemo.R
import com.example.booktourdemo.databinding.TrangChuScheduleCrudLayoutBinding
import com.example.booktourdemo.DatabaseAPI
import com.example.booktourdemo.firebase.OnDatabaseCallback
import com.example.booktourdemo.models.Tour.Schedule
import com.example.booktourdemo.models.Tour.Tour

class TrangChuScheduleFragment : Fragment() {
    private lateinit var chucNangTrangChuSchedule: ChucNangSchedule
    private lateinit var scheduleList: ArrayList<Schedule>
    private lateinit var filteredScheduleList: ArrayList<Schedule>
    private lateinit var adapter: RVCrudScheduleAdapter
    private lateinit var databaseAPI: DatabaseAPI
    private lateinit var binding: TrangChuScheduleCrudLayoutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = TrangChuScheduleCrudLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseAPI = DatabaseAPI(requireContext())
        scheduleList = ArrayList()
        filteredScheduleList = ArrayList()
        chucNangTrangChuSchedule = ChucNangSchedule(requireContext(), databaseAPI)

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.list.layoutManager = layoutManager

        adapter = RVCrudScheduleAdapter(requireContext(), filteredScheduleList, databaseAPI)
        binding.list.adapter = adapter

        setEvent()
        setContent()
    }

    private fun setEvent() {
        adapter.setOnMyItemClickListener(object : RVCrudScheduleAdapter.OnRecyclerviewItemClickListener {
            override fun onBtnXoa(view: View, position: Int) {
                val schedule = filteredScheduleList[position]
                chucNangTrangChuSchedule.xoaSchedule(schedule, position, filteredScheduleList, adapter)
            }

            override fun onBtnSua(view: View, position: Int) {
                val schedule = filteredScheduleList[position]

                val bundle = Bundle().apply {
                    putString("scheduleId", schedule.id)
                    putString("tourId", schedule.tourId)
                }
                val navController = findNavController()
                navController.navigate(R.id.action_trangChuScheduleFragment_to_suaScheduleFragment, bundle)
            }
        })
    }

    private fun setContent() {
        binding.edtTimKiem.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                chucNangTrangChuSchedule.filterSchedule(
                    query = query,
                    danhSachSchedule = scheduleList,
                    filteredScheduleList = filteredScheduleList,
                    adapter = adapter
                )
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun onResume() {
        super.onResume()
        scheduleList.clear()
        filteredScheduleList.clear()

        val tourTemp = ArrayList<Tour>()
        databaseAPI.docTatCaTours(tourTemp, object : OnDatabaseCallback {
            override fun onSuccess(temp: Any) {
                val tours = temp as List<Tour>

                var toursDone = 0 // đếm số tour đã load lịch trình xong
                for (tour in tours) {
                    databaseAPI.docTatCaLichTrinh(tour.id, object : OnDatabaseCallback {
                        override fun onSuccess(temp: Any) {
                            val schedules = temp as List<Schedule>
                            scheduleList.addAll(schedules)

                            toursDone++
                            if (toursDone == tours.size) {
                                filteredScheduleList.addAll(scheduleList)
                                adapter.notifyDataSetChanged()
                            }
                        }

                        override fun onFailure(e: Exception) {}
                    })
                }
            }

            override fun onFailure(e: Exception) {}
        })
    }
}