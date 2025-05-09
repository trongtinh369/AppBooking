package com.example.booktourdemo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.booktourdemo.databinding.RecyclerviewItemLayoutBinding
import com.example.booktourdemo.models.Tour.Tour

class RVTourAdapter(val context: Context, val tours: ArrayList<Tour>): RecyclerView.Adapter<RVTourAdapter.MyViewHolder>() {
    private var onItemClickListener: OnRecyclerviewItemClickListener? = null

    fun setOnMyItemClickListener(onItemClickListener: OnRecyclerviewItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun updateTours(newTours: List<Tour>){
        tours.clear()
        tours.addAll(newTours)
        notifyDataSetChanged()
    }

    interface OnRecyclerviewItemClickListener {
        fun onItemClick(idTour: String)
    }

    inner class MyViewHolder(val binding: RecyclerviewItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        var idTour = ""
        init {
            // Xử lý click trên toàn bộ item
            binding.root.setOnClickListener {
                onItemClickListener?.onItemClick(idTour)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RecyclerviewItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding) //  man hinh thuong
    }

    override fun getItemCount(): Int {
        return tours.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val tour = tours[position]
        holder.idTour = tour.id
        val binding = holder.binding
        binding.textTourName.text = tour.id
        Glide.with(binding.root.context)
            .load(tour.images[0])
            .into(binding.imageTour)
    }

}