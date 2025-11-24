package com.example.tracuuthietbi

import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class AddDeviceActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var deviceId: Int = -1
    private var selectedImageUri: Uri? = null

    private lateinit var imageViewPreview: ImageView

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        uri ->
        uri?.let {
            selectedImageUri = it
            Glide.with(this)
                .load(it)
                .into(imageViewPreview)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_device)

        dbHelper = DatabaseHelper(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        imageViewPreview = findViewById(R.id.image_view_preview)
        val buttonSelectImage = findViewById<Button>(R.id.button_select_image)
        val editTextDeviceName = findViewById<EditText>(R.id.edit_text_device_name)
        val spinnerDeviceType = findViewById<Spinner>(R.id.spinner_device_type)
        val switchAvailability = findViewById<Switch>(R.id.switch_availability)
        val editTextQuantity = findViewById<EditText>(R.id.edit_text_quantity)
        val buttonAdd = findViewById<Button>(R.id.button_add)

        val deviceTypes = arrayOf("Laptop", "Tablet", "Smartphone")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, deviceTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDeviceType.adapter = adapter

        deviceId = intent.getIntExtra("DEVICE_ID", -1)

        if (deviceId != -1) {
            supportActionBar?.title = "Sửa thiết bị"
            buttonAdd.text = "Cập nhật"
            dbHelper.getDevice(deviceId)?.let { device ->
                editTextDeviceName.setText(device.name)
                val typePosition = deviceTypes.indexOf(device.type)
                if (typePosition >= 0) {
                    spinnerDeviceType.setSelection(typePosition)
                }
                switchAvailability.isChecked = device.isAvailable
                editTextQuantity.setText(device.quantity.toString())
                device.imageUri?.let {
                    selectedImageUri = Uri.parse(it)
                    Glide.with(this)
                        .load(selectedImageUri)
                        .into(imageViewPreview)
                }
            }
        } else {
            supportActionBar?.title = "Thêm thiết bị"
        }

        buttonSelectImage.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        buttonAdd.setOnClickListener {
            val deviceName = editTextDeviceName.text.toString()
            val deviceType = spinnerDeviceType.selectedItem.toString()
            val isAvailable = switchAvailability.isChecked
            val quantity = editTextQuantity.text.toString().toIntOrNull() ?: 0

            if (deviceName.isBlank()) {
                Toast.makeText(this, "Vui lòng nhập tên thiết bị", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val device = Device(
                id = if(deviceId != -1) deviceId else 0,
                name = deviceName, 
                type = deviceType, 
                isAvailable = isAvailable, 
                quantity = quantity,
                imageUri = selectedImageUri?.toString()
            )

            if (deviceId != -1) {
                dbHelper.updateDevice(device)
                Toast.makeText(this, "Đã cập nhật thiết bị", Toast.LENGTH_SHORT).show()
            } else {
                dbHelper.addDevice(device)
                Toast.makeText(this, "Đã thêm thiết bị", Toast.LENGTH_SHORT).show()
            }
            finish() // Quay lại màn hình trước đó
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}