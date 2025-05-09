package com.example.booktourdemo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.booktourdemo.databinding.ItemCommentDetailBinding
import com.example.booktourdemo.databinding.ItemCommentLayoutBinding
import com.example.booktourdemo.models.Tour.Reply
import com.example.booktourdemo.models.Tour.Review

class RVCommentAdapter(
    val context: Context,
    val list: ArrayList<Review>,
    val replies: ArrayList<Reply>
) : RecyclerView.Adapter<RVCommentAdapter.MyViewHolder>() {

    private var selectedPosition = -1
    var onItemClickListener: OnRecyclerviewItemClickListener? = null

    fun setOnClickListener(listener: OnRecyclerviewItemClickListener) {
        this.onItemClickListener = listener
    }

    interface OnRecyclerviewItemClickListener {
        fun onLikeClick(idReview: String, position: Int)
        fun onRepLy(idReview: String, textComment: String, position: Int)
    }

    inner class MyViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun getItemViewType(position: Int): Int {
        return if (selectedPosition == position) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return if (viewType == 1) {
            val binding_detail = ItemCommentDetailBinding.inflate(LayoutInflater.from(context), parent, false)
            MyViewHolder(binding_detail)
        } else {
            val binding = ItemCommentLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
            MyViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val review = list[position]
        val timeAgo = System.currentTimeMillis() - review.created_at
        val listReply = replies.filter { it.idReview == review.id }

        if (getItemViewType(position) == 1) {
            val binding = holder.binding as ItemCommentDetailBinding
            with(binding) {
                textUsername.text = review.userName
                textCommentContent.text = review.comment
                textTimestamp.text = getTimeAgo(timeAgo)
                textLikeCount.text = "${review.likeCount} likes"
                textReplyCount.text = "${listReply.size} replies"

                Glide.with(context).load(review.avatar).into(imageAvatar)

                // Replies
                if (listReply.isNotEmpty()) {
                    recyclerViewReplies.visibility = View.VISIBLE
                    recyclerViewReplies.adapter = RVReplyAdapter(context, ArrayList(listReply))
                } else {
                    recyclerViewReplies.visibility = View.GONE
                }

                // Events
                btnReply.setOnClickListener {
                    selectedPosition = -1
                    notifyItemChanged(position)
                }

                btnLike.setOnClickListener {
                    onItemClickListener?.onLikeClick(review.id, position)
                }

                btnPostComment.setOnClickListener {
                    val text = editTextReply.text.toString()
                    onItemClickListener?.onRepLy(review.id, text, position)
                    notifyItemChanged(position)
                }
            }
        } else {
            val binding = holder.binding as ItemCommentLayoutBinding
            with(binding) {
                textUsername.text = review.userName
                textCommentContent.text = review.comment
                textTimestamp.text = getTimeAgo(timeAgo)
                textLikeCount.text = "${review.likeCount} likes"
                textReplyCount.text = "${listReply.size} replies"

                Glide.with(context).load(review.avatar).into(imageAvatar)

                // Events
                btnReply.setOnClickListener {
                    val index = position
                    if(selectedPosition == position){
                        selectedPosition = -1
                        notifyItemChanged(position)
                    }
                    else{
                        notifyItemChanged(selectedPosition)
                        selectedPosition = index
                        notifyItemChanged(selectedPosition)
                    }
                }

                btnLike.setOnClickListener {
                    onItemClickListener?.onLikeClick(review.id, position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
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
            days < 365 -> "${days / 30} tháng trước"
            else -> "${days / 365} năm trước"
        }
    }
}
