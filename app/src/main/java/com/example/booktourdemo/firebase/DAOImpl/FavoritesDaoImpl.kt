package com.example.booktourdemo.firebase.DAOImpl


import com.example.booktourdemo.firebase.FavoritesDao
import com.example.booktourdemo.models.Favorite
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FavoritesDaoImpl(private val db: FirebaseFirestore) : FavoritesDao {

    private val collection = db.collection("favorites")

    override suspend fun themTourYeuThich(favorite: Favorite): Boolean {
        return try {
            val docRef = collection.document(favorite.id)
            val snapshot = docRef.get().await()
            if (!snapshot.exists()) {
                docRef.set(favorite).await()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun xoaTourYeuThich(id: String): Boolean {
        return try {
            collection.document(id).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun kiemTraTourYeuThich(id: String): Boolean {
        return try {
            val snapshot = collection.document(id).get().await()
            snapshot.exists()
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun layDanhSachTourYeuThich(userId: String): List<Favorite> {
        return try {
            val snapshot = collection
                .whereEqualTo("userId", userId)
                .get()
                .await()
            snapshot.toObjects(Favorite::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

}
