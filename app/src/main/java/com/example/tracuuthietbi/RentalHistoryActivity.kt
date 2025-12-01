package com.example.tracuuthietbi

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class RentalHistoryActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rental_history)

        dbHelper = DatabaseHelper(this)

        val deviceId = intent.getIntExtra("DEVICE_ID", -1)
        val deviceName = intent.getStringExtra("DEVICE_NAME")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Lịch sử thuê: $deviceName"

        if (deviceId != -1) {
            val historyList = dbHelper.getRentalHistory(deviceId)
            val listView = findViewById<ListView>(R.id.list_view_rental_history)
            listView.adapter = RentalHistoryAdapter(this, historyList)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}