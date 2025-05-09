package com.example.booktourdemo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.booktourdemo.databinding.ItemReplyLayoutBinding
import com.example.booktourdemo.models.Tour.Reply

class RVReplyAdapter(val context: Context, private val replies: List<Reply>): RecyclerView.Adapter<RVReplyAdapter.ReplyViewHolder>() {

    var onItemClickListener: OnRecyclerviewItemClickListener? = null

    fun setOnClickListener(onItemClickListener: OnRecyclerviewItemClickListener){
        this.onItemClickListener = onItemClickListener
    }

    interface OnRecyclerviewItemClickListener {
        fun onLikeClick(idReview: String)
    }

    inner class ReplyViewHolder(val binding: ItemReplyLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        var idReply = ""
        init {
            binding.replyBtnLike.setOnClickListener {
                onItemClickListener?.onLikeClick(idReply)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyViewHolder {
        val binding = ItemReplyLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ReplyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return replies.size
    }

    override fun onBindViewHolder(holder: ReplyViewHolder, position: Int) {
        val reply = replies[position]
        holder.idReply = reply.id
        with(holder.binding) {
            replyUsername.text = reply.userName
            replyContent.text = reply.reply_text
            textLikeCount.text = "${reply.likeCount}"

            replyTimestamp.text = getTimeAgo(System.currentTimeMillis() - reply.created_at)

            Glide.with(context)
                .load(reply.avatar)
                .into(replyAvatar)
        }
    }

    private fun getTimeAgo(timeAgoMillis: Long): String {
        val seconds = timeAgoMillis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            seconds < 60 -> "Vừa xong"
            minutes < 60 -> "$minutes phút trước"
            hours < 24 -> "$hours giờ trước"
            days < 7 -> "$days ngày trước"
            days < 30 -> "${days / 7} tuần trước"
            else -> "${days / 30} tháng trước"
        }
    }
}
