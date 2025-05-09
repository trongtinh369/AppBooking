package com.example.booktourdemo

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.booktourdemo.firebase.OnDatabaseCallback
import com.example.booktourdemo.models.Tour.Reply
import com.example.booktourdemo.models.Tour.Review

class test_activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.test_layout)
        val databaseAPI = DatabaseAPI(this)
        var a = ArrayList<Review>()
        var b = ArrayList<Reply>()
        var c = Review("","Nam Nam","a",5,"ahehe",3,52)
        var t = Reply("","Nam Nam","aheheheh",3,"a","TcHW59qO6vj3WR9K9j1J", 5)


    }
}