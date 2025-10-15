package com.example.tracuuthietbi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonAddDevice = findViewById<Button>(R.id.button_add_device)
        val buttonSearchDevice = findViewById<Button>(R.id.button_search_device)
        val buttonListDevices = findViewById<Button>(R.id.button_list_devices)

        buttonAddDevice.setOnClickListener {
            val intent = Intent(this, AddDeviceActivity::class.java)
            startActivity(intent)
        }

        buttonSearchDevice.setOnClickListener {
            val intent = Intent(this, SearchDevicesActivity::class.java)
            startActivity(intent)
        }

        buttonListDevices.setOnClickListener {
            val intent = Intent(this, ListDevicesActivity::class.java)
            startActivity(intent)
        }
    }
}