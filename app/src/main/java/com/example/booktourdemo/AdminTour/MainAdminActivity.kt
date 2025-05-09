package com.example.booktourdemo.AdminTour

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.booktourdemo.R
import com.example.booktourdemo.databinding.MainAdminLayoutBinding

class MainAdminActivity : AppCompatActivity() {
    private lateinit var binding: MainAdminLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainAdminLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tìm NavHostFragment và NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_admin_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Liên kết BottomNavigationView với NavController
        binding.bottomNavigationView.setupWithNavController(navController)

        // Thiết lập sự kiện bấm vào Home để điều hướng về TrangChuFragment
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Home -> {
                    navController.navigate(R.id.trangChuFragment)
                    true
                }
                R.id.Manager -> {
                    navController.navigate(R.id.quanLiFragment)
                    true
                }
                R.id.Schedule ->{
                    navController.navigate(R.id.trangchuScheduleFragment)
                    true
                }
                else -> false
            }
        }
    }
}