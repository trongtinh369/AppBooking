package com.example.booktourdemo.manager.trangchu

import com.example.booktourdemo.adapters.RVSchedulesAdapter
import com.example.booktourdemo.adapters.RVTopTourBanner
import com.example.booktourdemo.adapters.RVTourAdapter
import com.example.booktourdemo.databinding.TrangChuLayoutBinding
import com.example.booktourdemo.models.Tour.Schedule
import com.example.booktourdemo.models.Tour.Tour

class SearchTrangChu() {

    fun setupSearch(
        tours:ArrayList<Tour>,
        listGoc: ArrayList<Tour>,
        adapterTour: RVTourAdapter,
        toptours: ArrayList<Tour>,
        listTopTourGoc: ArrayList<Tour>,
        adapterTopTour: RVTopTourBanner,
        schedules: ArrayList<Schedule>,
        listScheduleGoc: ArrayList<Schedule>,
        adapterSche: RVSchedulesAdapter,
        binding: TrangChuLayoutBinding,
    ){
        binding.edtSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val keyword = s.toString().trim().lowercase()

                tours.clear()
                toptours.clear()
                schedules.clear()

                if (keyword.isEmpty()) {
                    tours.addAll(listGoc)
                    toptours.addAll(listTopTourGoc)
                    schedules.addAll(listScheduleGoc)
                } else {
                    // Lọc tours dựa trên từ khóa
                    tours.addAll(listGoc.filter { tour ->
                        tour.title.lowercase().contains(keyword) ||
                                tour.description.lowercase().contains(keyword) ||
                                tour.id.lowercase().contains(keyword)
                    })
                    toptours.addAll(listTopTourGoc.filter { tour ->
                        tour.title.lowercase().contains(keyword) ||
                                tour.description.lowercase().contains(keyword) ||
                                tour.id.lowercase().contains(keyword)
                    })
                    schedules.addAll(listScheduleGoc.filter { schedule ->
                        schedule.tourId.lowercase().contains(keyword) ||
                                schedule.id.lowercase().contains(keyword)
                    })
                }

                adapterTour.notifyDataSetChanged()
                adapterTopTour.notifyDataSetChanged()
                adapterSche.notifyDataSetChanged()
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }
}