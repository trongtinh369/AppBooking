package com.example.booktourdemo.activitis

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.booktourdemo.DatabaseAPI
import com.example.booktourdemo.manager.api.dangnhapSDK.FacebookSignInHelper
import com.example.booktourdemo.manager.api.dangnhapSDK.GoogleSignInHelper
import com.example.booktourdemo.manager.api.dangnhapSDK.LoginCallback
import com.example.booktourdemo.firebase.*
import com.example.booktourdemo.databinding.DangNhapLayoutBinding
import com.example.booktourdemo.models.User

class DangNhapActivity : AppCompatActivity() {
    private lateinit var binding: DangNhapLayoutBinding
    private lateinit var databaseAPI: DatabaseAPI
    private lateinit var googleSignInHelper: GoogleSignInHelper
    private lateinit var facebookSignInHelper: FacebookSignInHelper
    companion object{
        private val ROLE_USER = "user"
        var idUser = "nppnam05@gmail.com"
        var nameUser = "Nam Nam"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DangNhapLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseAPI = DatabaseAPI(this)

        googleSignInHelper = GoogleSignInHelper(this) // đăng nhập bằng Google
        googleSignInHelper.setGoogleLoginCallback(object : LoginCallback {
            override fun onLoginSuccess(email: String, name: String, avatarUrl: String) {
                dangNhapHoacDangKyUser(email, name, avatarUrl)
            }

            override fun onLoginFailure(errorMessage: String) {
                Toast.makeText(this@DangNhapActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }

        })

        facebookSignInHelper = FacebookSignInHelper(this) // đăng nhập bằng Facebook
        facebookSignInHelper.setFacebookLoginCallback(object : LoginCallback {
            override fun onLoginSuccess(email: String, name: String, avatarUrl: String) {
                dangNhapHoacDangKyUser(email, name, avatarUrl)
            }

            override fun onLoginFailure(errorMessage: String) {
                Toast.makeText(this@DangNhapActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })

        setEvent()
    }

    private fun setEvent() {
        binding.btnGoogleLogin.setOnClickListener { googleSignInHelper.signIn() } //  gọi Event đăng nhập google
        binding.facebookLoginButton.setOnClickListener { facebookSignInHelper.signIn() } // gọi Event đăng nhập facebook
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // API trả Intern về mang theo dữ liệu
        super.onActivityResult(requestCode, resultCode, data)
        googleSignInHelper.handleResult(requestCode, data)
        facebookSignInHelper.handleResult(requestCode, resultCode, data)
    }

    private fun dangNhapHoacDangKyUser(email: String, name: String, avatarUrl: String) {
        // Kiểm tra nếu user đã đăng ký chưa trong cơ sở dữ liệu
        databaseAPI.kiemTraUserDaDangKyChua(email, object : OnDatabaseCallback {
            override fun onSuccess(userId: Any) {
                userId as String
                if (userId.isNotEmpty()) {
                    // User đã tồn tại, tiến hành đăng nhập
                    idUser = userId
                    dangNhapThanhCong()
                } else {
                    val createdAt: Long = System.currentTimeMillis()
                    // User chưa tồn tại, tiến hành tạo mới
                    idUser = email
                    val newUser = User(id = email,name = name, email = email, url_avatar = avatarUrl, role = ROLE_USER, created_at = createdAt)
                    databaseAPI.themUser(newUser, object : OnDatabaseCallback {
                        override fun onSuccess(newId: Any) {
                            // Tạo user thành công
                            dangNhapThanhCong()
                        }

                        override fun onFailure(e: Exception) {
                            Log.e("User", "Thêm user thất bại", e)
                        }
                    })
                }
            }

            override fun onFailure(e: Exception) {
                Log.e("User", "Lỗi khi kiểm tra user", e)
            }
        })

    }

    fun dangNhapThanhCong(){
        Toast.makeText(this@DangNhapActivity, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this@DangNhapActivity, MainActivity::class.java))
        finish()
    }
}
