package com.example.booktourdemo.firebase.DAOImpl.Tour

import android.util.Log
import com.example.booktourdemo.firebase.DAO.Tour.ReviewsDAO
import com.example.booktourdemo.models.Tour.Review
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class ReviewsDAOImpl(private val db: FirebaseFirestore) : ReviewsDAO {

    // Lấy collection tours
    private val toursCollection = db.collection("tours")


    override suspend fun docTatCaReviewsTheoTourID(idTour: String): List<Review> {
        return try {
            val reviewsSnapshot = toursCollection
                .document(idTour)
                .collection("reviews")
                .get()
                .await()

            reviewsSnapshot.toObjects(Review::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun docReviewTheoId(idTour: String, idReview: String): Review? {
        return try {
            val reviewSnapshot = toursCollection
                .document(idTour)
                .collection("reviews")
                .document(idReview)
                .get()
                .await()

            reviewSnapshot.toObject(Review::class.java)
        } catch (e: Exception) {
            null
        }
    }


    override suspend fun themReview(tourId: String, review: Review): Boolean {
        return try {
            // Tạo reviewId ngẫu nhiên nếu chưa có
            val reviewId = toursCollection.document(tourId).collection("reviews").document().id
            review.id = reviewId // gán lại nếu cần
            val reviewRef = toursCollection.document(tourId).collection("reviews").document(reviewId)
            // 2. Lưu review
            reviewRef.set(review).await()
            true
        } catch (e: Exception) {
            false
        }
    }



    override suspend fun capNhatReview(tourId: String, review: Review): Boolean {
        return try {
            val reviewRef = toursCollection.document(tourId).collection("reviews").document(review.id)
            reviewRef.set(review, SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun xoaReview(tourId: String, reviewId: String): Boolean {
        return try {
            val reviewRef = toursCollection.document(tourId).collection("reviews").document(reviewId)
            reviewRef.delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
