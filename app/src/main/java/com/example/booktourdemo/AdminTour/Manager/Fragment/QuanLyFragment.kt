package com.example.booktourdemo.AdminTour.Manager.Fragment

import android.content.Intent
import com.example.booktourdemo.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.booktourdemo.activitis.DangNhapActivity
import com.example.booktourdemo.activitis.MainActivity
import com.example.booktourdemo.databinding.QuanLyLayoutTBinding

class QuanLyFragment : Fragment() {
    private var _binding: QuanLyLayoutTBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = QuanLyLayoutTBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEvent()
    }

    private fun setEvent() {
        binding.btnThem.setOnClickListener {
            findNavController().navigate(R.id.action_quanLy_to_themTour)
        }
        binding.btnThemSchedule.setOnClickListener {
            findNavController().navigate(R.id.action_quanLy_to_themSchedule)
        }
        binding.btnThoat.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            // FLAG_ACTIVITY_CLEAR_TASK đã xóa toàn bộ stack
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}