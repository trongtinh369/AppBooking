package com.example.booktourdemo.manager.api.dangnhapSDK

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException

class GoogleSignInHelper(private val activity: Activity) {
    private val googleSignInClient: GoogleSignInClient
    private var googleLoginCallback: LoginCallback? = null

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    fun setGoogleLoginCallback(googleLoginCallback: LoginCallback) {
        this.googleLoginCallback = googleLoginCallback
    }


    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) // cấu hình loại đăng nhập cũng như yêu cầu lấy cái gì từ acc đó
            .requestEmail() // yêu cầu lấy email từ tài khoản người dùng
            .requestProfile()
            .build()
        googleSignInClient = GoogleSignIn.getClient(activity, gso) // là đối tượng dùng để Quản lý cấu hình, Điều khiển việc hiện màn hình đăng nhập Google
    }

    fun signIn() {
        googleSignInClient.signOut().addOnCompleteListener { // singOut để nó out acc google trước đó, "listener" để đợi khi signOut() thực hiện xong thì mình mới làm tiếp hành động tiếp theo
            val signInIntent = googleSignInClient.signInIntent //  tạo ra một Intent đặc biệt để mở giao diện đăng nhập Google.
            activity.startActivityForResult(signInIntent, RC_SIGN_IN) // Gửi Intent đó để mở giao diện đăng nhập
        }
    }

    fun handleResult(requestCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data) // hàm của Google SDK dùng để lấy kết quả đăng nhập từ Intent mà Google trả về
            try {
                val account = task.getResult(ApiException::class.java) // Lấy kết từ đăng nhập, trả về đối tượng GoogleSignInAccount
                // Nếu thất bại -> ném ra một lỗi ApiException
                account?.let {
                    val email = it.email ?: ""
                    val name = it.displayName ?: ""
                    val avatarUrl = it.photoUrl?.toString() ?: ""

                    if (email.isNotEmpty() && name.isNotEmpty()) {
                        googleLoginCallback?.onLoginSuccess(email, name, avatarUrl)
                    } else {
                        googleLoginCallback?.onLoginFailure("Không lấy được thông tin từ Google Account")
                    }
                }
            } catch (e: ApiException) {
                googleLoginCallback?.onLoginFailure("Đăng nhập Google thất bại: ${e.statusCode}")
            }
        }
    }
}
