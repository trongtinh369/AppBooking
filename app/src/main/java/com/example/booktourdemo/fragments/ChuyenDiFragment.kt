package com.example.booktourdemo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.booktourdemo.R
import com.example.booktourdemo.databinding.ChuyenDiLayoutBinding

class ChuyenDiFragment : Fragment() {
    private var _binding: ChuyenDiLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ChuyenDiLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Thiết lập Toolbar
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Thiết lập NavController cho BottomNavigationView
        setupBottomNav()
    }

    private fun setupBottomNav() {
        val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment_chuyendi) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavChuyenDi.setupWithNavController(navController)
    }

    override fun onStart() {
        super.onStart()
        // Đặt mục mặc định mỗi khi Fragment được hiển thị
        binding.bottomNavChuyenDi.selectedItemId = R.id.nav_upcoming_trips
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}