package com.example.booktourdemo.AdminTour

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booktourdemo.R
import com.example.booktourdemo.databinding.TrangChuTourLayoutBinding
import com.example.booktourdemo.DatabaseAPI
import com.example.booktourdemo.firebase.OnDatabaseCallback
import com.example.booktourdemo.models.Tour.Tour
import com.example.booktourdemo.AdminTour.adapter.RVCrudTourAdapter
import com.example.booktourdemo.AdminTour.Manager.ChucNangTrangChu.ChucNangTrangChu

class TrangChuTourFragment : Fragment() {
    private val RQ_SPEECH = 102
    private lateinit var chucNangTrangChu: ChucNangTrangChu
    private var _binding: TrangChuTourLayoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var danhSachTours: ArrayList<Tour>
    private lateinit var filteredProductList: ArrayList<Tour>
    private lateinit var adapter: RVCrudTourAdapter
    private lateinit var databaseAPI: DatabaseAPI

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TrangChuTourLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseAPI = DatabaseAPI(requireContext())
        danhSachTours = ArrayList()
        filteredProductList = ArrayList()
        chucNangTrangChu = ChucNangTrangChu(requireContext(), databaseAPI)

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.list.layoutManager = layoutManager

        adapter = RVCrudTourAdapter(requireContext(), filteredProductList, databaseAPI)
        binding.list.adapter = adapter

        setEvent()
        setContent()
    }

    private fun setEvent() {
        adapter.setOnMyItemClickListener(object : RVCrudTourAdapter.OnRecyclerviewItemClickListener {
            override fun onBtnXoa(view: View, position: Int) {
                val tour = filteredProductList[position]
                chucNangTrangChu.xoaTour(tour, position, filteredProductList, adapter)
            }
            override fun onBtnSua(view: View, position: Int) {
                val tourId = filteredProductList[position]
                val bundle = Bundle().apply { putString("tour_id", tourId.id) }
                val navController = findNavController()
                navController.navigate(R.id.action_trangChu_to_suaTour, bundle)
            }
        })
    }

    private fun setContent() {
        binding.edtTimKiem.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                chucNangTrangChu.filterTours(
                    query = query,
                    danhSachTours = danhSachTours,
                    filteredProductList = filteredProductList,
                    adapter = adapter
                )
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun onResume() {
        super.onResume()
        danhSachTours.clear()
        filteredProductList.clear()
        databaseAPI.docTatCaTours(danhSachTours, object : OnDatabaseCallback {
            override fun onSuccess(temp: Any) {
                filteredProductList.addAll(danhSachTours)
                adapter.notifyDataSetChanged()
            }
            override fun onFailure(e: Exception) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}