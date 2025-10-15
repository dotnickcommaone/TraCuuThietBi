package com.example.tracuuthietbi

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddDeviceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_device)

        val editTextDeviceName = findViewById<EditText>(R.id.edit_text_device_name)
        val spinnerDeviceType = findViewById<Spinner>(R.id.spinner_device_type)
        val buttonAdd = findViewById<Button>(R.id.button_add)

        val deviceTypes = arrayOf("Laptop", "Tablet", "Smartphone")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, deviceTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDeviceType.adapter = adapter

        buttonAdd.setOnClickListener {
            val deviceName = editTextDeviceName.text.toString()
            val deviceType = spinnerDeviceType.selectedItem.toString()

            val message = "Added $deviceName of type $deviceType"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}