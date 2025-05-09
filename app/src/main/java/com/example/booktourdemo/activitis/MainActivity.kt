package com.example.booktourdemo.activitis

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.booktourdemo.databinding.MainLayoutBinding
import com.example.booktourdemo.DatabaseAPI
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPaySDK

class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainLayoutBinding
    private lateinit var databaseAPI: DatabaseAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseAPI = DatabaseAPI(this)


        // StrictMode Policy
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX)


        setupBottomNav()

    }
    // Phương thức để cung cấp DatabaseAPI cho các Fragment
    fun getDatabaseAPI(): DatabaseAPI {
        return databaseAPI
    }

    fun setupBottomNav(){
        // Tìm NavHostFragment và NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(com.example.booktourdemo.R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Liên kết BottomNavigationView với NavController
        binding.bottomNav.setupWithNavController(navController)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        ZaloPaySDK.getInstance().onResult(intent)
    }
}