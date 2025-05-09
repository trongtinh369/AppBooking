package com.example.booktourdemo.manager.trangchu

import android.content.Context
import android.content.Intent
import com.example.booktourdemo.activitis.ItemDetailActivity
import com.example.booktourdemo.activitis.LoaiTourActivity
import com.example.booktourdemo.adapters.RVSchedulesAdapter
import com.example.booktourdemo.adapters.RVTopTourBanner
import com.example.booktourdemo.adapters.RVTourAdapter

class EventClickItemTrangChu(private val context: Context) {
    fun EventClick(adapterTopTour: RVTopTourBanner, adapterTour: RVTourAdapter, adapterSche: RVSchedulesAdapter){
        adapterTopTour.setOnMyItemClickListener(object : RVTopTourBanner.OnRecyclerviewItemClickListener {
            override fun onItemClick(idTour: String) {
                val intern = Intent(context, LoaiTourActivity::class.java)
                intern.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                intern.putExtra("idTour", idTour)
                context.startActivity(intern)
            }
        })

        adapterTour.setOnMyItemClickListener(object : RVTourAdapter.OnRecyclerviewItemClickListener{
            override fun onItemClick(idTour: String) {
                val intern = Intent(context, LoaiTourActivity::class.java)
                intern.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                intern.putExtra("idTour", idTour)
                context.startActivity(intern)
            }
        })

        adapterSche.setOnMyItemClickListener(object : RVSchedulesAdapter.OnRecyclerviewItemClickListener {
            override fun onItemClick(idTour: String, idSchedule: String) {
                val intern = Intent(context, ItemDetailActivity::class.java)
                intern.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                intern.putExtra("idTour", idTour)
                intern.putExtra("idSchedule", idSchedule)
                context.startActivity(intern)
            }

        })
    }
}