package com.example.tracuuthietbi

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class UserActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var deviceAdapter: DeviceAdapter
    private var deviceList: MutableList<Device> = mutableListOf()
    private var currentUsername: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "User View"

        currentUsername = intent.getStringExtra("USERNAME")
        dbHelper = DatabaseHelper(this)

        val listViewDevices = findViewById<ListView>(R.id.list_view_devices)
        deviceAdapter = DeviceAdapter(this, deviceList)
        listViewDevices.adapter = deviceAdapter

        listViewDevices.setOnItemClickListener { _, _, position, _ ->
            val selectedDevice = deviceList[position]
            val intent = Intent(this, DeviceDetailActivity::class.java)
            intent.putExtra("DEVICE_ID", selectedDevice.id)
            intent.putExtra("USERNAME", currentUsername)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sort_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_all -> {
                refreshDeviceList(null)
                return true
            }
            R.id.sort_laptop -> {
                refreshDeviceList("Laptop")
                return true
            }
            R.id.sort_tablet -> {
                refreshDeviceList("Tablet")
                return true
            }
            R.id.sort_smartphone -> {
                refreshDeviceList("Smartphone")
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        refreshDeviceList(null) // Tải lại toàn bộ danh sách khi quay lại
    }

    private fun refreshDeviceList(type: String?) {
        val newDeviceList = if (type == null) {
            dbHelper.getAllDevices()
        } else {
            dbHelper.getDevicesByType(type)
        }
        deviceList.clear()
        deviceList.addAll(newDeviceList)
        deviceAdapter.notifyDataSetChanged()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}