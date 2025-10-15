package com.example.tracuuthietbi

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class ListDevicesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_devices)

        val listViewDevices = findViewById<ListView>(R.id.list_view_devices)

        val devices = mutableListOf<String>()
        // Sample Data
        devices.add("Laptops")
        devices.add("  - Dell XPS 15")
        devices.add("  - MacBook Pro 16")
        devices.add("Tablets")
        devices.add("  - iPad Pro")
        devices.add("  - Samsung Galaxy Tab S8")

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, devices)
        listViewDevices.adapter = adapter
    }
}