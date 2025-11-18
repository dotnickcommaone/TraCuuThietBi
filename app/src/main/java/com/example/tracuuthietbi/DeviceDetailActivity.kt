package com.example.tracuuthietbi

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DeviceDetailActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var deviceId: Int = -1

    private lateinit var textViewDeviceName: TextView
    private lateinit var textViewAvailability: TextView
    private lateinit var textViewRentalStatus: TextView
    private lateinit var textViewQuantity: TextView
    private lateinit var buttonApplyRental: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_detail)

        dbHelper = DatabaseHelper(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        textViewDeviceName = findViewById(R.id.text_view_device_name)
        textViewAvailability = findViewById(R.id.text_view_availability)
        textViewRentalStatus = findViewById(R.id.text_view_rental_status)
        textViewQuantity = findViewById(R.id.text_view_quantity)
        buttonApplyRental = findViewById(R.id.button_apply_rental)

        deviceId = intent.getIntExtra("DEVICE_ID", -1)
        if (deviceId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID thiết bị", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // **Kiểm tra xem có phải Admin không**
        val isAdminView = intent.getBooleanExtra("IS_ADMIN_VIEW", false)
        if (isAdminView) {
            buttonApplyRental.visibility = View.GONE // Ẩn nút nếu là Admin
        } else {
            buttonApplyRental.setOnClickListener {
                handleRentalApplication()
            }
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
        textViewRentalStatus.text = if (device.isAvailable) "Trạng thái thuê: Sẵn sàng cho thuê" else "Trạng thái thuê: Hiện đang được thuê"
        textViewQuantity.text = "Số lượng còn lại: ${device.quantity}"
    }

    private fun handleRentalApplication() {
        val currentDevice = dbHelper.getDevice(deviceId)
        if (currentDevice != null && currentDevice.isAvailable && currentDevice.quantity > 0) {
            val newQuantity = currentDevice.quantity - 1
            val updatedDevice = currentDevice.copy(
                quantity = newQuantity,
                isAvailable = newQuantity > 0
            )
            dbHelper.updateDevice(updatedDevice)
            updateUi(updatedDevice)
            Toast.makeText(this, "Bạn đã đăng ký thuê ${updatedDevice.name}", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Thiết bị này hiện không có sẵn để thuê", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}