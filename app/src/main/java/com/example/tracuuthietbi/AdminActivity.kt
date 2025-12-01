package com.example.tracuuthietbi

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.ListView
import android.widget.PopupMenu
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

        // **Thay thế Long Click bằng Short Click để hiển thị menu**
        listViewDevices.setOnItemClickListener { _, view, position, _ ->
            val selectedDevice = deviceList[position]
            showPopupMenu(view, selectedDevice)
        }
    }

    private fun showPopupMenu(view: View, device: Device) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.device_context_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
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
                else -> false
            }
        }
        popup.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.admin_menu, menu)
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
        refreshDeviceList(null)
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
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_delete, null)
        val checkBoxConfirm = dialogView.findViewById<CheckBox>(R.id.checkbox_confirm_delete)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.text_view_dialog_message)

        dialogMessage.text = "Bạn có chắc chắn muốn xóa thiết bị '${device.name}' không?"

        val dialog = AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                dbHelper.deleteDevice(device.id)
                refreshDeviceList(null)
                Toast.makeText(this, "Đã xóa thiết bị", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hủy", null)
            .create()

        dialog.setOnShowListener { 
            val okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            okButton.isEnabled = false

            checkBoxConfirm.setOnCheckedChangeListener { _, isChecked ->
                okButton.isEnabled = isChecked
            }
        }

        dialog.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}