package com.example.booktourdemo.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.booktourdemo.manager.trangchu.EventClickItemTrangChu
import com.example.booktourdemo.manager.trangchu.ReadData
import com.example.booktourdemo.manager.trangchu.SearchTrangChu
import com.example.booktourdemo.adapters.RVSchedulesAdapter
import com.example.booktourdemo.adapters.RVTourAdapter
import com.example.booktourdemo.databinding.TrangChuLayoutBinding
import com.example.booktourdemo.DatabaseAPI
import com.example.booktourdemo.activitis.DangNhapActivity
import com.example.booktourdemo.adapters.RVTopTourBanner
import com.example.booktourdemo.firebase.OnDatabaseCallback
import com.example.booktourdemo.models.Tour.Schedule
import com.example.booktourdemo.models.Tour.Tour
import com.example.booktourdemo.models.User
import com.google.firebase.firestore.FirebaseFirestore

class TrangChuFragment : Fragment() {
    private var _binding: TrangChuLayoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseAPI: DatabaseAPI
    private lateinit var adapterTour: RVTourAdapter
    private lateinit var adapterTopTour: RVTopTourBanner
    private lateinit var adapterSche: RVSchedulesAdapter
    private lateinit var tours: ArrayList<Tour>
    private lateinit var schedules: ArrayList<Schedule>
    private lateinit var listTourGoc: ArrayList<Tour>
    private lateinit var topTours: ArrayList<Tour>
    private lateinit var search: SearchTrangChu
    private lateinit var event: EventClickItemTrangChu
    private lateinit var readData: ReadData
    private lateinit var listTopTourGoc: ArrayList<Tour>
    private lateinit var listScheduleGoc: ArrayList<Schedule>
    private val userId = DangNhapActivity.idUser // Thống nhất tên biến


    private val handler = Handler(Looper.getMainLooper())
    private var topToursCurrentPage = 0
    private val autoScrollDelay = 3000L // 3 giây

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TrangChuLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo các đối tượng
        tours = ArrayList()
        listScheduleGoc = ArrayList()
        listTourGoc = ArrayList()
        schedules = ArrayList()
        topTours = ArrayList()
        listTopTourGoc = ArrayList()
        val db = FirebaseFirestore.getInstance()
        databaseAPI = DatabaseAPI(requireContext())
        search = SearchTrangChu()
        event = EventClickItemTrangChu(requireContext())
        readData = ReadData(databaseAPI)

        // Khởi tạo adapter
        adapterTopTour = RVTopTourBanner(requireContext(), topTours)
        adapterTour = RVTourAdapter(requireContext(), tours)
        adapterSche = RVSchedulesAdapter(requireContext(), tours, schedules)

        // Gán adapter và setup cho ViewPager2
        binding.topToursViewpager.adapter = adapterTopTour
        binding.topToursViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        // Gán adapter và LayoutManager cho RecyclerView
        binding.rvTourCategories.adapter = adapterTour
        binding.rvTourCategories.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.rvTourTypes.adapter = adapterSche
        binding.rvTourTypes.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        event()
        startAutoScroll()
    }

    fun event() {
        // Xử lý sự kiện
        search.setupSearch(tours, listTourGoc, adapterTour, topTours, listTopTourGoc, adapterTopTour, schedules, listScheduleGoc, adapterSche, binding)
        event.EventClick(adapterTopTour, adapterTour, adapterSche)
    }

    override fun onResume() {
        super.onResume()
        readData.ReadTrangChu(listTourGoc, listTopTourGoc, listScheduleGoc, adapterTopTour, adapterTour, adapterSche)
        startAutoScroll()

        databaseAPI.layUserQuaID(userId, object : OnDatabaseCallback {
            override fun onSuccess(temp: Any) {
                val user = temp as User
                with(binding) {
                    Glide.with(requireContext())
                        .load(user.url_avatar)
                        .into(avatarUser)
                    txtWelcome.text = "Xin chào, ${user.name}"
                }
            }
            override fun onFailure(e: Exception) {}
        })
    }

    // tự động cuộn banner
    private fun startAutoScroll() {
        val topToursRunnable = object : Runnable {
            override fun run() {
                if (adapterTopTour.itemCount > 0) {
                    topToursCurrentPage = (topToursCurrentPage + 1) % adapterTopTour.itemCount
                    binding.topToursViewpager.currentItem = topToursCurrentPage
                    handler.postDelayed(this, autoScrollDelay)
                }
            }
        }
        handler.postDelayed(topToursRunnable, autoScrollDelay)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null) // Dừng tự động cuộn
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        _binding = null
    }
}