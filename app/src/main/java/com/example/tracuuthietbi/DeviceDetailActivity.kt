package com.example.tracuuthietbi

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class DeviceDetailActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var deviceId: Int = -1
    private var currentUsername: String? = null

    private lateinit var imageViewDevice: ImageView
    private lateinit var textViewDeviceName: TextView
    private lateinit var textViewAvailability: TextView
    private lateinit var textViewRentalStatus: TextView
    private lateinit var textViewQuantity: TextView
    private lateinit var buttonAction: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_detail)

        dbHelper = DatabaseHelper(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // ... (khởi tạo UI giữ nguyên)

        deviceId = intent.getIntExtra("DEVICE_ID", -1)
        currentUsername = intent.getStringExtra("USERNAME")

        if (deviceId == -1) { /* ... */ }

        val isAdminView = intent.getBooleanExtra("IS_ADMIN_VIEW", false)
        if (isAdminView) { buttonAction.visibility = View.GONE }
    }

    override fun onResume() { /* ... */ }

    private fun loadDeviceData() { /* ... */ }

    private fun updateUi(device: Device) { /* ... */ }

    private fun handleRentDevice(device: Device) {
        if (currentUsername == null) return

        if (dbHelper.isUserRentingDevice(currentUsername!!)) {
            Toast.makeText(this, "Bạn chỉ có thể thuê 1 thiết bị tại một thời điểm", Toast.LENGTH_LONG).show()
            return
        }

        val newQuantity = device.quantity - 1
        val updatedDevice = device.copy(
            quantity = newQuantity,
            isAvailable = newQuantity > 0,
            rented_by_user = currentUsername
        )
        dbHelper.updateDevice(updatedDevice)
        dbHelper.addRentalRecord(device.id, currentUsername!!) // **Ghi lịch sử thuê**

        updateUi(updatedDevice)
        Toast.makeText(this, "Thuê thành công!", Toast.LENGTH_SHORT).show()
    }

    private fun handleReturnDevice(device: Device) {
        if (currentUsername == null) return

        val newQuantity = device.quantity + 1
        val updatedDevice = device.copy(
            quantity = newQuantity,
            isAvailable = true,
            rented_by_user = null
        )
        dbHelper.updateDevice(updatedDevice)
        dbHelper.updateReturnDate(device.id, currentUsername!!) // **Cập nhật lịch sử trả**

        updateUi(updatedDevice)
        Toast.makeText(this, "Đã trả thiết bị!", Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}