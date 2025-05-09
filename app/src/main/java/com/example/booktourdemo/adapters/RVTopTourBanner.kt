package com.example.booktourdemo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.booktourdemo.databinding.ItemTopTourBinding
import com.example.booktourdemo.databinding.RecyclerviewItemLayoutBinding
import com.example.booktourdemo.models.Tour.Tour

class RVTopTourBanner(val context: Context, val tours: ArrayList<Tour>) : RecyclerView.Adapter<RVTopTourBanner.MyViewHolder>() {
    private var onItemClickListener: OnRecyclerviewItemClickListener? = null

    fun setOnMyItemClickListener(onItemClickListener: OnRecyclerviewItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun updateTours(newTours: List<Tour>) {
        tours.clear()
        tours.addAll(newTours)
        notifyDataSetChanged()
    }

    interface OnRecyclerviewItemClickListener {
        fun onItemClick(idTour: String)
    }

    inner class MyViewHolder(val binding: ItemTopTourBinding) : RecyclerView.ViewHolder(binding.root) {
        var idTour = ""
        init {
            // Xử lý click trên toàn bộ item
            binding.root.setOnClickListener {
                onItemClickListener?.onItemClick(idTour)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemTopTourBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val tour = tours[position % tours.size] // Lặp lại nếu hết dữ liệu (cho ViewPager2)
        holder.idTour = tour.id
        val binding = holder.binding
        binding.textTourName.text = tour.id
        Glide.with(binding.root.context)
            .load(tour.images[0])
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_close_clear_cancel)
            .into(binding.imageTour)
    }

    override fun getItemCount(): Int {
        return if (tours.isEmpty()) 0 else Int.MAX_VALUE // Lặp vô hạn cho ViewPager2
    }
}