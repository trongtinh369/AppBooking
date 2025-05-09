package com.example.booktourdemo.manager.itemdetail

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.booktourdemo.DatabaseAPI
import com.example.booktourdemo.adapters.RVCommentAdapter
import com.example.booktourdemo.databinding.ItemDetailLayoutBinding
import com.example.booktourdemo.firebase.OnDatabaseCallback
import com.example.booktourdemo.models.Booking.Booking
import com.example.booktourdemo.models.Booking.Payment
import com.example.booktourdemo.models.Tour.Reply
import com.example.booktourdemo.models.Tour.Review
import com.example.booktourdemo.models.Tour.Schedule
import com.example.booktourdemo.models.Tour.Tour
import com.example.booktourdemo.models.User
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

class ItemDetailManager(
    private val context: Context,
    private val binding: ItemDetailLayoutBinding,
    private val idTour: String,
    private val idSchedule: String,
    private val IDuser: String,
) {
    private val databaseAPI = DatabaseAPI(context)
    private lateinit var tour: Tour
    private lateinit var sche: Schedule
    private lateinit var user: User
    private val reviews = ArrayList<Review>()
    private val replies = ArrayList<Reply>()
    private val adapterComment = RVCommentAdapter(context, reviews, replies)

    fun setupRecyclerView() {
        binding.recyclerComments.adapter = adapterComment
        binding.recyclerComments.layoutManager = LinearLayoutManager(context)
    }

    fun loadData() {
        loadUser()
        loadTour()
        loadSchedule()
        loadReviews()
        loadReplies()
    }

    fun setEventListeners() {
        binding.fabBookTour.setOnClickListener {
            val bottomSheet = NumberOfPeopleBottomSheet()
            bottomSheet.setOnConfirmListener { numberOfPeople ->
                if (numberOfPeople > sche.available_slots) {
                    Toast.makeText(context, "Số người vượt quá", Toast.LENGTH_SHORT).show()
                } else {
                    sche.available_slots -= numberOfPeople
                    updateBooking(numberOfPeople)
                }
            }
            bottomSheet.show((context as AppCompatActivity).supportFragmentManager, "NumberOfPeopleBottomSheet")
        }

        binding.btnPostComment.setOnClickListener {
            postComment()
        }

        adapterComment.setOnClickListener(object : RVCommentAdapter.OnRecyclerviewItemClickListener {
            override fun onLikeClick(idReview: String, position: Int) {
                likeReview(idReview, position)
            }

            override fun onRepLy(idReview: String, textComment: String, position: Int) {
                if (textComment.isNotEmpty()) {
                    val reply = Reply("", user.name, textComment, 0, user.url_avatar, idReview, System.currentTimeMillis())
                    replies.add(reply)
                    adapterComment.notifyItemChanged(position)

                    databaseAPI.themTraLoiDanhGia(idTour, idReview, reply, object : OnDatabaseCallback {
                        override fun onSuccess(temp: Any) {}

                        override fun onFailure(e: Exception) {}
                    })
                }
            }
        })
    }

    private fun loadUser() {
        databaseAPI.layUserQuaID(IDuser, object : OnDatabaseCallback {
            override fun onSuccess(temp: Any) {
                user = temp as User
            }

            override fun onFailure(e: Exception) {}
        })
    }

    private fun loadTour() {
        databaseAPI.docTourTheoID(idTour, object : OnDatabaseCallback {
            override fun onSuccess(temp: Any) {
                tour = temp as Tour
                displayTourInfo()
            }

            override fun onFailure(e: Exception) {}
        })
    }

    private fun displayTourInfo() {
        val imageUrl = if (tour.images.size > 1) tour.images[1] else tour.images[0]

        with(binding) {
            textDescription.text = tour.description
            textDuration.text = "${calculateDaysBetween(sche.start_date, sche.end_date)} ngày"
            textPrice.text = "VND ${String.format("%,.0f", tour.price)}"
            collapsingToolbar.title = tour.title
            textLocation.text = tour.location.name
            textCity.text = tour.location.city
            textCountry.text = tour.location.country
            Glide.with(context).load(imageUrl).into(tourImage)
        }
    }

    private fun loadSchedule() {
        databaseAPI.docLichTrinhTheoID(idTour, idSchedule, object : OnDatabaseCallback {
            override fun onSuccess(temp: Any) {
                sche = temp as Schedule
                with(binding) {
                    textMaxPeople.text = "Tối đa ${sche.available_slots} người"
                    textStartDate.text = "Ngày bắt đầu: ${formatDate(sche.start_date)}"
                    textEndDate.text = "Ngày kết thúc: ${formatDate(sche.end_date)}"
                }
            }

            override fun onFailure(e: Exception) {}
        })
    }

    private fun loadReviews() {
        databaseAPI.docTatCaReviewsTheoTourID(idTour, reviews, object : OnDatabaseCallback {
            override fun onSuccess(temp: Any) {
                adapterComment.notifyDataSetChanged()
            }

            override fun onFailure(e: Exception) {}
        })
    }

    private fun loadReplies() {
        databaseAPI.docTatCaRepliesTheoTourId(idTour, replies, object : OnDatabaseCallback {
            override fun onSuccess(temp: Any) {
                adapterComment.notifyDataSetChanged()
            }

            override fun onFailure(e: Exception) {}
        })
    }

    private fun postComment() {
        val comment = binding.txtComment.text.toString()
        binding.txtComment.clearFocus()
        binding.txtComment.text.clear()
        if (comment.isNotEmpty()) {
            val review = Review("", user.name, user.url_avatar, 5, comment, 0, System.currentTimeMillis())
            reviews.add(review)
            adapterComment.notifyDataSetChanged()
            databaseAPI.themReview(idTour, review, object : OnDatabaseCallback {
                override fun onSuccess(temp: Any) {}

                override fun onFailure(e: Exception) {}
            })
        }
    }

    private fun likeReview(idReview: String, position: Int) {
        reviews[position].likeCount++
        adapterComment.notifyItemChanged(position)

        databaseAPI.docReviewTheoID(idTour, idReview, object : OnDatabaseCallback {
            override fun onSuccess(temp: Any) {
                (temp as Review).likeCount++
                databaseAPI.capNhapReview(idTour, temp, object : OnDatabaseCallback {
                    override fun onSuccess(temp: Any) {}

                    override fun onFailure(e: Exception) {}
                })
            }

            override fun onFailure(e: Exception) {}
        })
    }

    private fun updateBooking(numP: Int) {
        val totalPrice = numP * tour.price
        val booking = Booking(
            sche.close_day, System.currentTimeMillis(), sche.end_date, "", true, numP,
            Payment(totalPrice, "zalopay", false), sche.id, sche.start_date,
            totalPrice, tour.id, IDuser
        )

        val listBooking = ArrayList<Booking>()
        databaseAPI.layDanhSachBookingTheoUser(IDuser, listBooking, object : OnDatabaseCallback {
            override fun onSuccess(temp: Any) {
                val existingBooking = listBooking.find { it.schedule_id == sche.id }
                if (existingBooking != null) {
                    existingBooking.num_people += numP
                    existingBooking.payment.amount += totalPrice
                    existingBooking.total_price += totalPrice
                    databaseAPI.capNhapBooking(existingBooking, object : OnDatabaseCallback {
                        override fun onSuccess(temp: Any) {}
                        override fun onFailure(e: Exception) {}
                    })
                } else {
                    databaseAPI.themBooking(booking, object : OnDatabaseCallback {
                        override fun onSuccess(temp: Any) {}
                        override fun onFailure(e: Exception) {}
                    })
                }
            }

            override fun onFailure(e: Exception) {}
        })

        databaseAPI.capNhatLichTrinh(tour.id, sche.id, sche, object : OnDatabaseCallback {
            override fun onSuccess(temp: Any) {
                binding.textMaxPeople.text = "Tối đa ${sche.available_slots} người"
            }

            override fun onFailure(e: Exception) {}
        })
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun calculateDaysBetween(startDateMillis: Long, endDateMillis: Long): Long {
        val start = Instant.ofEpochMilli(startDateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
        val end = Instant.ofEpochMilli(endDateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
        return ChronoUnit.DAYS.between(start, end)
    }
}

