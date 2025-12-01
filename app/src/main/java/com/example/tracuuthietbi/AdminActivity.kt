package com.example.tracuuthietbi

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class AdminActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var deviceAdapter: DeviceAdapter
    private var deviceList: MutableList<Device> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_devices)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Admin View"

        dbHelper = DatabaseHelper(this)

        val listViewDevices = findViewById<ListView>(R.id.list_view_devices)
        deviceAdapter = DeviceAdapter(this, deviceList)
        listViewDevices.adapter = deviceAdapter

        registerForContextMenu(listViewDevices)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Thêm menu của Admin (nút +)
        menuInflater.inflate(R.menu.admin_menu, menu)
        // Thêm menu sắp xếp
        menuInflater.inflate(R.menu.sort_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_device -> {
                startActivity(Intent(this, AddDeviceActivity::class.java))
                return true
            }
            R.id.sort_all -> {
                refreshDeviceList(null) // null để tải tất cả
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

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val device = deviceList[info.position]

        return when (item.itemId) {
            R.id.action_view_details -> {
                val intent = Intent(this, DeviceDetailActivity::class.java)
                intent.putExtra("DEVICE_ID", device.id)
                intent.putExtra("IS_ADMIN_VIEW", true)
                startActivity(intent)
                true
            }
            R.id.action_edit_device -> {
                val intent = Intent(this, AddDeviceActivity::class.java)
                intent.putExtra("DEVICE_ID", device.id)
                startActivity(intent)
                true
            }
            R.id.action_delete_device -> {
                showDeleteConfirmationDialog(device)
                true
            }
            R.id.action_view_rental_history -> {
                val intent = Intent(this, RentalHistoryActivity::class.java)
                intent.putExtra("DEVICE_ID", device.id)
                intent.putExtra("DEVICE_NAME", device.name)
                startActivity(intent)
                true
            }
            else -> super.onContextItemSelected(item)
        }
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

    private fun showDeleteConfirmationDialog(device: Device) {
        // ... (hàm này giữ nguyên)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}