package com.example.tracuuthietbi

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class UserActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var deviceAdapter: DeviceAdapter
    private var deviceList: MutableList<Device> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "User View"

        dbHelper = DatabaseHelper(this)

        val listViewDevices = findViewById<ListView>(R.id.list_view_devices)
        deviceAdapter = DeviceAdapter(this, deviceList)
        listViewDevices.adapter = deviceAdapter

        listViewDevices.setOnItemClickListener { _, _, position, _ ->
            val selectedDevice = deviceList[position]
            val intent = Intent(this, DeviceDetailActivity::class.java)
            intent.putExtra("DEVICE_ID", selectedDevice.id)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshDeviceList()
    }

    private fun refreshDeviceList() {
        val newDeviceList = dbHelper.getAllDevices()
        deviceList.clear()
        deviceList.addAll(newDeviceList)
        deviceAdapter.notifyDataSetChanged()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}