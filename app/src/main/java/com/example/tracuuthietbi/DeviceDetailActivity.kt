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
    private var isAdminView: Boolean = false

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

        imageViewDevice = findViewById(R.id.image_view_device_detail)
        textViewDeviceName = findViewById(R.id.text_view_device_name)
        textViewAvailability = findViewById(R.id.text_view_availability)
        textViewRentalStatus = findViewById(R.id.text_view_rental_status)
        textViewQuantity = findViewById(R.id.text_view_quantity)
        buttonAction = findViewById(R.id.button_apply_rental)

        deviceId = intent.getIntExtra("DEVICE_ID", -1)
        currentUsername = intent.getStringExtra("USERNAME")
        isAdminView = intent.getBooleanExtra("IS_ADMIN_VIEW", false)

        if (deviceId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID thiết bị", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        if (isAdminView) {
            buttonAction.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        loadDeviceData()
    }

    private fun loadDeviceData() {
        val device = dbHelper.getDevice(deviceId)
        if (device == null) {
            Toast.makeText(this, "Không thể tải thông tin thiết bị", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        updateUi(device)
    }

    private fun updateUi(device: Device) {
        supportActionBar?.title = device.name
        textViewDeviceName.text = device.name
        textViewAvailability.text = if (device.isAvailable) "Tình trạng: Có sẵn" else "Tình trạng: Đã được thuê"
        textViewRentalStatus.text = if (device.rented_by_user != null) "Người thuê: ${device.rented_by_user}" else "Trạng thái thuê: Sẵn sàng cho thuê"
        textViewQuantity.text = "Số lượng còn lại: ${device.quantity}"

        device.imageUri?.let { Glide.with(this).load(Uri.parse(it)).into(imageViewDevice) }

        if (!isAdminView) {
            if (device.rented_by_user == currentUsername) {
                buttonAction.text = "Trả thiết bị"
                buttonAction.isEnabled = true
                buttonAction.setOnClickListener { handleReturnDevice(device) }
            } else {
                buttonAction.text = "Đăng ký thuê"
                buttonAction.isEnabled = device.isAvailable && device.quantity > 0
                buttonAction.setOnClickListener { handleRentDevice(device) }
            }
        }
    }

    private fun handleRentDevice(device: Device) {
        if (currentUsername == null) return

        // **THAY ĐỔI LOGIC KIỂM TRA**
        if (dbHelper.isUserRentingDeviceFromCategory(currentUsername!!, device.type)) {
            Toast.makeText(this, "Bạn đã thuê một thiết bị khác thuộc loại '${device.type}'", Toast.LENGTH_LONG).show()
            return
        }

        val newQuantity = device.quantity - 1
        val updatedDevice = device.copy(
            quantity = newQuantity,
            isAvailable = newQuantity > 0,
            rented_by_user = currentUsername
        )
        dbHelper.updateDevice(updatedDevice)
        dbHelper.addRentalRecord(device.id, currentUsername!!)

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
        dbHelper.updateReturnDate(device.id, currentUsername!!)

        updateUi(updatedDevice)
        Toast.makeText(this, "Đã trả thiết bị!", Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}