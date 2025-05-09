package com.example.booktourdemo.firebase.DAO
import com.example.booktourdemo.models.Tour.Reply

interface ReviewRepliesDAO {

    suspend fun themTraLoiDanhGia(tourId: String, reviewId: String, reviewReply: Reply): Boolean

    suspend fun capNhatTraLoiDanhGiaDanhGia(tourId: String, reviewId: String, reply: Reply): Boolean

    suspend fun xoaTraLoiDanhGia(tourId: String, reviewId: String, replyId: String): Boolean

    suspend fun docTatCaRepliesTheoTourId(tourId: String): List<Reply>
}
