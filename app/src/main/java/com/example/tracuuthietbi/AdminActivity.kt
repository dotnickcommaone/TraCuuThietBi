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

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.device_context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val device = deviceList[info.position]

        return when (item.itemId) {
            R.id.action_view_details -> {
                val intent = Intent(this, DeviceDetailActivity::class.java)
                intent.putExtra("DEVICE_ID", device.id)
                intent.putExtra("IS_ADMIN_VIEW", true) // **Thêm tín hiệu**
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
            else -> super.onContextItemSelected(item)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.admin_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_device -> {
                startActivity(Intent(this, AddDeviceActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
                refreshDeviceList()
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