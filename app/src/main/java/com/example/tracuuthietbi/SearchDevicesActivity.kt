package com.example.tracuuthietbi

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SearchDevicesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_devices)

        val editTextDeviceName = findViewById<EditText>(R.id.edit_text_device_name)
        val spinnerDeviceType = findViewById<Spinner>(R.id.spinner_device_type)
        val buttonSearch = findViewById<Button>(R.id.button_search)

        val deviceTypes = arrayOf("All", "Laptop", "Tablet", "Smartphone")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, deviceTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDeviceType.adapter = adapter

        buttonSearch.setOnClickListener {
            val deviceName = editTextDeviceName.text.toString()
            val deviceType = spinnerDeviceType.selectedItem.toString()

            val message = "Searching for $deviceName of type $deviceType"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}