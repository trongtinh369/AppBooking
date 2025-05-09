package com.example.booktourdemo.AdminTour.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.booktourdemo.databinding.ItemTourLayoutBinding
import com.example.booktourdemo.DatabaseAPI
import com.example.booktourdemo.models.Tour.Tour
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RVCrudTourAdapter(val context: Context, val list: ArrayList<Tour>, val databaseAPI: DatabaseAPI): RecyclerView.Adapter<RVCrudTourAdapter.MyViewHolder>() {
    private var onItemClickListener: OnRecyclerviewItemClickListener? = null

    interface OnRecyclerviewItemClickListener {
        fun onBtnXoa(view: View,position: Int)
        fun onBtnSua(view: View,position: Int)
    }

    fun setOnMyItemClickListener(onItemClickListener: OnRecyclerviewItemClickListener){
        this.onItemClickListener = onItemClickListener
    }

    inner class MyViewHolder(val binding: ItemTourLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(onItemClickListener: OnRecyclerviewItemClickListener?){
            binding.btnXoa.setOnClickListener {
                onItemClickListener?.onBtnXoa(it,adapterPosition)
            }
            binding.btnSuaThongTin.setOnClickListener {
                onItemClickListener?.onBtnSua(it,adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemTourLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val tour = list[position]

        Glide.with((holder.binding as ItemTourLayoutBinding).root.context)
            .load(tour.images[0])
            .into((holder.binding as ItemTourLayoutBinding).imgAnhTour)
        (holder.binding as ItemTourLayoutBinding).tvTenTour.text = tour.title
        (holder.binding as ItemTourLayoutBinding).tvDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(tour.created_at))
        (holder.binding as ItemTourLayoutBinding).tvGiaTien.text = "${tour.price} đ"
        (holder.binding as ItemTourLayoutBinding).tvDiaDiem.text = tour.location.city

        holder.bind(onItemClickListener)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}