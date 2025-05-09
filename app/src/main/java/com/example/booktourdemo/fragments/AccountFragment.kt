package com.example.booktourdemo.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.booktourdemo.activitis.DangNhapActivity
import com.example.booktourdemo.AdminTour.MainAdminActivity
import com.example.booktourdemo.databinding.AccountLayoutBinding
import com.example.booktourdemo.DatabaseAPI
import com.example.booktourdemo.firebase.OnDatabaseCallback
import com.example.booktourdemo.models.User

class AccountFragment : Fragment() {
    private var _binding: AccountLayoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var user: User
    private lateinit var databaseAPI: DatabaseAPI
    private val idUser = DangNhapActivity.idUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AccountLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = User()
        databaseAPI = DatabaseAPI(requireContext())
        setupEventListeners()
    }

    private fun setupEventListeners() {
        // Xử lý click nút Trang Admin
        binding.buttonAdminPage.setOnClickListener {
            val intent = Intent(requireContext(), MainAdminActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
        }

        // Xử lý click nút Đăng Xuất
        binding.buttonLogout.setOnClickListener {
            val intent = Intent(requireContext(), DangNhapActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            // Không cần requireActivity().finish() vì FLAG_ACTIVITY_CLEAR_TASK đã xóa toàn bộ stack
        }
    }

    override fun onResume() {
        super.onResume()
        databaseAPI.layUserQuaID(idUser, object : OnDatabaseCallback {
            override fun onSuccess(temp: Any) {
                user = temp as User
                with(binding) {
                    textName.text = user.name
                    textEmail.text = user.email

                    Glide.with(this@AccountFragment)
                        .load(user.url_avatar)
                        .into(imageAvatar)
                }
                if (user.role.lowercase() == "admin") {
                    binding.buttonAdminPage.visibility = View.VISIBLE
                } else {
                    binding.buttonAdminPage.visibility = View.GONE
                }
            }

            override fun onFailure(e: Exception) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}