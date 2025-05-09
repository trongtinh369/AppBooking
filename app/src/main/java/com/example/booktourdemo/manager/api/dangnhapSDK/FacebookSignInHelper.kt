package com.example.booktourdemo.manager.api.dangnhapSDK
import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.example.booktourdemo.manager.api.dangnhapSDK.models.FacebookAPI
import com.example.booktourdemo.manager.api.dangnhapSDK.models.FacebookUser
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FacebookSignInHelper(private val activity: Activity) {
    private val callbackManager = CallbackManager.Factory.create() // quản lý kết quả trả về từ Facebook SDK sau khi đăng nhập.
    private lateinit var facebookApi: FacebookAPI

    private var facebookLoginCallback: LoginCallback? = null

    fun setFacebookLoginCallback(facebookCallback: LoginCallback){
        this.facebookLoginCallback = facebookCallback
    }

    init {
        FacebookSdk.fullyInitialize() // 	Khởi tạo SDK của Facebook
        LoginManager.getInstance().logOut() // Đăng xuất acc trước đó
        AccessToken.setCurrentAccessToken(null) // tránh tự động đăng nhập lại
        Profile.setCurrentProfile(null) // 	Reset thông tin hồ sơ người dùng nếu có
    }

    // Facebook SDK xử lý các kết quả của quá trình đăng nhập
    // truyền kết quả về SDK Facebook -> SDK gọi lại các callback đã đăng ký qua registerCallback
    fun handleResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    fun signIn() {
        LoginManager.getInstance().logInWithReadPermissions(activity, listOf("email", "public_profile")) //  đăng nhập người dùng Facebook và xin quyền (permissions)
        // Facebook trả về loginResult chứa AccessToken.

        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                val accessToken = loginResult.accessToken.token // mã xác thực để gọi các API lấy thông tin từ Facebook

                //Khởi tạo Retrofit để gọi API Graph của Facebook
                val retrofit = Retrofit.Builder()
                    .baseUrl(FacebookAPI.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                facebookApi = retrofit.create(FacebookAPI::class.java)
                //Gửi request để lấy thông tin người dùng
                val call = facebookApi.getUserInfo(accessToken)

                // Xử lý response trả về bất đồng bộ bằng enqueue
                call.enqueue(object : Callback<FacebookUser> {
                    override fun onResponse(call: Call<FacebookUser>, response: Response<FacebookUser>) {
                        if (response.isSuccessful) {
                            val user = response.body()
                            if (user != null && user?.id != null) {
                                val email = user?.email ?: user.id
                                val name = user?.name ?: "name${user.id}"
                                val avatarUrl = "https://graph.facebook.com/${user.id}/picture?type=large"
                                facebookLoginCallback?.onLoginSuccess(email , name, avatarUrl)
                            }
                        } else {
                            facebookLoginCallback?.onLoginFailure("Lỗi khi lấy thông tin người dùng.")
                        }
                    }

                    override fun onFailure(call: Call<FacebookUser>, t: Throwable) {
                        facebookLoginCallback?.onLoginFailure("Không thể lấy thông tin người dùng.")
                    }
                })
            }

            // người dùng tự thoát Login
            override fun onCancel() {
                Toast.makeText(activity, "Đăng nhập Facebook bị hủy", Toast.LENGTH_SHORT).show()
            }

            //  lỗi kỹ thuật xảy ra trong quá trình đăng nhập
            override fun onError(error: FacebookException) {
                Toast.makeText(activity, "Đăng nhập Facebook thất bại", Toast.LENGTH_SHORT).show()
            }
        })
    }
}