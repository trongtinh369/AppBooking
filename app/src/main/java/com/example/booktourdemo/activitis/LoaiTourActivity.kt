package com.example.booktourdemo.activitis

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booktourdemo.adapters.RVSchedulesAdapter
import com.example.booktourdemo.databinding.LoaiTourLayoutBinding
import com.example.booktourdemo.DatabaseAPI
import com.example.booktourdemo.firebase.OnDatabaseCallback
import com.example.booktourdemo.models.Tour.Schedule
import com.example.booktourdemo.models.Tour.Tour

class LoaiTourActivity : AppCompatActivity() {
    private lateinit var binding: LoaiTourLayoutBinding
    private lateinit var databaseAPI: DatabaseAPI
    private lateinit var tours: ArrayList<Tour>
    private lateinit var schedules: ArrayList<Schedule>
    private lateinit var adapterSche: RVSchedulesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoaiTourLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // tạo các đối tượng cơ bản
        databaseAPI = DatabaseAPI(this)
        tours = ArrayList()
        schedules = ArrayList()

        // khởi tạo adapter
        adapterSche = RVSchedulesAdapter(this, tours, schedules)

        // Gán adapter và LayoutManager cho RecyclerView và setup RV
        binding.rvTourTypes.adapter = adapterSche
        binding.rvTourTypes.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        event()
    }

    fun event(){
        adapterSche.setOnMyItemClickListener(object : RVSchedulesAdapter.OnRecyclerviewItemClickListener{
            override fun onItemClick(idTour: String, idSchedule: String) {
                val intern = Intent(this@LoaiTourActivity, ItemDetailActivity::class.java)
                intern.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                intern.putExtra("idTour", idTour)
                intern.putExtra("idSchedule", idSchedule)
                startActivity(intern)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        var idTour = intent.getStringExtra("idTour") ?: "Can Tho"
        var array = ArrayList<Tour>()

        databaseAPI.docDangKyDangMoTheoIDTour(idTour, array, object : OnDatabaseCallback{
            override fun onSuccess(temp: Any) {
                adapterSche.updateSchedules(array)
                binding.toolbar.title = idTour
            }

            override fun onFailure(e: Exception) {}

        })


    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent
    }
}