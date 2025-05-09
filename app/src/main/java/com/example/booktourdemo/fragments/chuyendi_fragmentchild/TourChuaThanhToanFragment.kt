package com.example.booktourdemo.fragments.chuyendi_fragmentchild

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booktourdemo.activitis.DangNhapActivity
import com.example.booktourdemo.activitis.ItemDetailActivity
import com.example.booktourdemo.adapters.RVChuyenDiChuaThanhToan
import com.example.booktourdemo.activitis.MainActivity
import com.example.booktourdemo.databinding.TourChuaThanhToanLayoutBinding
import com.example.booktourdemo.DatabaseAPI
import com.example.booktourdemo.firebase.OnDatabaseCallback
import com.example.booktourdemo.models.Booking.Booking
import com.example.booktourdemo.models.Tour.Schedule
import com.example.booktourdemo.models.Tour.Tour
import com.example.booktourdemo.manager.chuyendi.Payment

class TourChuaThanhToanFragment : Fragment() {
    private var _binding: TourChuaThanhToanLayoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RVChuyenDiChuaThanhToan
    private lateinit var tours: ArrayList<Tour>
    private lateinit var bookings: ArrayList<Booking>
    private lateinit var databaseAPI: DatabaseAPI
    private lateinit var userID: String
    private lateinit var payment: Payment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TourChuaThanhToanLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up các đối tượng
        tours = ArrayList()
        bookings = ArrayList()
        databaseAPI = (requireActivity() as MainActivity).getDatabaseAPI()
        userID = DangNhapActivity.idUser
        payment = Payment(requireActivity())



        // Khởi tạo adapter
        adapter = RVChuyenDiChuaThanhToan(requireContext(), tours, bookings)

        // Gán adapter và LayoutManager cho RecyclerView
        binding.rvChuaThanhToan.adapter = adapter
        binding.rvChuaThanhToan.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // Thiết lập sự kiện
        setupEvents()
    }

    private fun setupEvents() {
        adapter.setOnMyItemClickListener(object : RVChuyenDiChuaThanhToan.OnRecyclerviewItemClickListener {
            override fun onItemClick(idTour: String, idSchedule: String) {
                val intent = Intent(requireContext(), ItemDetailActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                intent.putExtra("idTour", idTour)
                intent.putExtra("idSchedule", idSchedule)
                startActivity(intent)
            }

            override fun onPayment(idTour: String, oldBooking: Booking) {
                payment.menthodPayment(oldBooking,databaseAPI, {
                    loadData()}, // cap nhap lai du lieu sau khi pay xong
                    {
                    updateTourStatic(idTour) // them count static sau khi pay
                })
                // update static chưa làm

            }

            override fun onHuy(booking: Booking) {
                // xóa lịch trình
                databaseAPI.xoaBooking(booking.id, object : OnDatabaseCallback{
                    override fun onSuccess(temp: Any) {
                        // load lại dữ liệu
                        loadData()

                        // đọc lịch trình lên ddể cập nhập lại lịch trình
                        databaseAPI.docLichTrinhTheoID(booking.tour_id, booking.schedule_id, object : OnDatabaseCallback{
                            override fun onSuccess(temp: Any) {

                                val sche = temp as Schedule
                                sche.available_slots += booking.num_people


                                databaseAPI.capNhatLichTrinh(booking.tour_id, booking.schedule_id, sche, object : OnDatabaseCallback{
                                    override fun onSuccess(temp: Any) {}

                                    override fun onFailure(e: Exception) {}

                                })
                            }
                            override fun onFailure(e: Exception) {}
                        })
                    }
                    override fun onFailure(e: Exception) {}

                })
            }
        })
    }
    fun updateTourStatic(idTour: String){
        databaseAPI.docTourTheoID(idTour, object : OnDatabaseCallback{
            override fun onSuccess(temp: Any) {
                temp as Tour
                temp.statistics.booking_count++
                val updates = mapOf("statistics.booking_count" to temp.statistics.booking_count)
                databaseAPI.capNhatTour(temp.id,updates, object : OnDatabaseCallback{
                    override fun onSuccess(temp: Any) {}

                    override fun onFailure(e: Exception) {}

                })
            }

            override fun onFailure(e: Exception) {}

        })
    }

    fun loadData(){
        var bookingList = ArrayList<Booking>()
        var tourList = ArrayList<Tour>()
        databaseAPI.layDanhSachBookingTheoUser(userID, bookingList, object : OnDatabaseCallback {
            override fun onSuccess(temp: Any) {

                var idTours = bookingList.filter { it.payment.status == false }.map { it.tour_id }
                var newBookings = bookingList.filter { it.payment.status == false }

                if(idTours.isNotEmpty()){
                    databaseAPI.docToursTheoListID(idTours, tourList, object :
                        OnDatabaseCallback {
                        override fun onSuccess(temp: Any) {
                            adapter.updateChuyenDi(tourList, newBookings)
                        }
                        override fun onFailure(e: Exception) {}
                    })
                }
                else{
                    adapter.updateChuyenDi(emptyList(), emptyList())
                }
            }
            override fun onFailure(e: Exception) {}

        })
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}