package com.example.booktourdemo.activitis

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.booktourdemo.databinding.ItemDetailLayoutBinding
import com.example.booktourdemo.manager.itemdetail.ItemDetailManager


class ItemDetailActivity : AppCompatActivity() {
    private lateinit var binding: ItemDetailLayoutBinding
    private lateinit var manager: ItemDetailManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ItemDetailLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idTour = intent.getStringExtra("idTour") ?: "Can Tho"
        val idSchedule = intent.getStringExtra("idSchedule") ?: "sAI8kcagtHHJ6KCHnb01"
        val IDuser = DangNhapActivity.idUser

        manager = ItemDetailManager(this, binding, idTour, idSchedule, IDuser)

        manager.setupRecyclerView()
        manager.setEventListeners()
    }

    override fun onResume() {
        super.onResume()
        manager.loadData()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent
    }
}
