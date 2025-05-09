package com.example.booktourdemo.firebase.DAOImpl.Tour

import android.util.Log
import com.example.booktourdemo.firebase.DAO.ReviewRepliesDAO
import com.example.booktourdemo.models.Tour.Reply
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class ReviewRepliesDAOImpl(private val db: FirebaseFirestore) : ReviewRepliesDAO {

    private val collection = db.collection("tours")

    override suspend fun docTatCaRepliesTheoTourId(tourId: String): List<Reply> = coroutineScope {
        try {
            val repliesList = mutableListOf<Reply>()

            val tourDoc = collection.document(tourId).get().await()

            val reviewsSnapshot = tourDoc.reference.collection("reviews").get().await()

            // Tạo list các async task để đọc replies song song
            val deferredReplies = reviewsSnapshot.documents.map { reviewDoc ->
                async {
                    try {
                        val repliesSnapshot = reviewDoc.reference.collection("replies").get().await()
                        repliesSnapshot.toObjects(Reply::class.java)
                    } catch (e: Exception) {
                        emptyList<Reply>() // Nếu 1 review lỗi thì bỏ qua
                    }
                }
            }

            // Đợi tất cả hoàn thành
            deferredReplies.forEach { deferred ->
                repliesList.addAll(deferred.await())
            }

            repliesList
        } catch (e: Exception) {
            Log.d("test", "$e")
            emptyList()
        }
    }


    override suspend fun themTraLoiDanhGia(tourId: String, reviewId: String, reply: Reply): Boolean {
        return try {
            val replyRef = collection
                .document(tourId)
                .collection("reviews")
                .document(reviewId)
                .collection("replies")
                .document() // Tự động tạo ID

            // Gán ID ngược lại vào đối tượng reply
            reply.id = replyRef.id

            replyRef.set(reply).await()
            true
        } catch (e: Exception) {
            false
        }
    }


    override suspend fun capNhatTraLoiDanhGiaDanhGia(tourId: String, reviewId: String, reply: Reply): Boolean {
        return try {
            val replyRef = collection
                .document(tourId)
                .collection("reviews")
                .document(reviewId)
                .collection("replies")
                .document(reply.id)

            replyRef.set(reply, SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun xoaTraLoiDanhGia(tourId: String, reviewId: String, replyId: String): Boolean {
        return try {
            val replyRef = collection
                .document(tourId)
                .collection("reviews")
                .document(reviewId)
                .collection("replies")
                .document(replyId)

            replyRef.delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

}
