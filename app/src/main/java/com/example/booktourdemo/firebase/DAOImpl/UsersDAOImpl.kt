package com.example.booktourdemo.firebase.DAOImpl
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.booktourdemo.models.User
import com.example.booktourdemo.firebase.DAO.UsersDAO
import com.google.firebase.firestore.SetOptions

class UsersDAOImpl(private val db: FirebaseFirestore) : UsersDAO {

    private val collection = db.collection("users")

    override suspend fun getUserById(id: String): User? {
        return try {
            val doc = collection.document(id).get().await()
            if (doc.exists()) doc.toObject(User::class.java)
            else null
        } catch (e: Exception) {
            null
        }
    }


    override suspend fun addUser(user: User): Boolean {
        return try {
            val docRef = collection.document(user.email)
            val snapshot = docRef.get().await()
            if (!snapshot.exists()) { // Kiểm tra xem cái User này đã tồn tại chưa, nếu chưa thì add
                docRef.set(user).await()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateUser(id: String, user: User): Boolean {
        return try {
            collection.document(id).set(user, SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteUser(id: String): Boolean {
        return try {
            collection.document(id).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }


    override suspend fun CheckUser(email: String): String {
        return try {
            val doc = collection.document(email).get().await()
            if (doc.exists()) {
                doc.id// Trả về email luôn vì đó là document ID
            } else {
                ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}
