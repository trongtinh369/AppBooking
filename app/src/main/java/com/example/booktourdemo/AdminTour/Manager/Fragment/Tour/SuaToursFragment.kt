package com.example.booktourdemo.AdminTour.Manager.Fragment.Tour

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.booktourdemo.DatabaseAPI
import com.example.booktourdemo.firebase.OnDatabaseCallback
import com.example.booktourdemo.models.Tour.Schedule
import com.example.booktourdemo.models.Tour.Tour
import com.example.booktourdemo.R
import com.example.booktourdemo.databinding.SuaToursLayoutBinding


class SuaToursFragment : Fragment() {

    private lateinit var binding: SuaToursLayoutBinding
    private lateinit var databaseAPI: DatabaseAPI
    private lateinit var tourList: ArrayList<Tour>
    private lateinit var scheduleList: ArrayList<Schedule>
    private lateinit var tourId: String
    private var tourDangChinhSua: Tour? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = SuaToursLayoutBinding.inflate(inflater, container, false)

        tourList = ArrayList()
        scheduleList = ArrayList()
        databaseAPI = DatabaseAPI(requireContext())

        setEvent()

        return binding.root
    }

    private fun setEvent() {
        binding.btnLuuThongTin.setOnClickListener {
            suaTour()
        }
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        tourId = arguments?.getString("tour_id") ?: ""

        databaseAPI.docTourTheoID(tourId, object : OnDatabaseCallback {
            override fun onSuccess(temp: Any) {
                if (temp is Tour) {
                    tourDangChinhSua = temp
                    val tour = temp
                    Glide.with(binding.root.context).load(tour.images[0]).into(binding.imgTour)
                    binding.edtTenTour.setText(tour.title)
                    binding.edtMoTa.setText(tour.description)
                    binding.edtGiaTour.setText(tour.price.toString())
                    binding.edtSoNgayTour.setText(tour.duration.toString())
                }
            }
            override fun onFailure(e: Exception) {}
        })
    }

    private fun suaTour() {
        val updates = mapOf<String, Any>(
            "title" to binding.edtTenTour.text.toString().trim(),
            "description" to binding.edtMoTa.text.toString().trim(),
            "price" to (binding.edtGiaTour.text.toString().toDoubleOrNull() ?: 0.0),
            "duration" to (binding.edtSoNgayTour.text.toString().toIntOrNull() ?: 0)
        )

        databaseAPI.capNhatTour(tourId, updates, object : OnDatabaseCallback {
            override fun onSuccess(temp: Any) {
                Toast.makeText(requireContext(), "Sửa thành công!", Toast.LENGTH_SHORT).show()
                val navController = findNavController()
                navController.navigate(R.id.action_suaTour_to_trangChu)
            }
            override fun onFailure(e: Exception) {}
        })
    }
}
