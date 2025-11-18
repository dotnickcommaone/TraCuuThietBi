package com.example.tracuuthietbi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editTextUsername = findViewById<EditText>(R.id.edit_text_username)
        val editTextPassword = findViewById<EditText>(R.id.edit_text_password)
        val buttonLogin = findViewById<Button>(R.id.button_login)

        buttonLogin.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()

            when {
                username == "admin" && password == "admin" -> {
                    // Đăng nhập với quyền admin
                    Toast.makeText(this, "Đăng nhập thành công với quyền Admin", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, AdminActivity::class.java)
                    startActivity(intent)
                }
                username == "user" && password == "user" -> {
                    // Đăng nhập với quyền user
                    Toast.makeText(this, "Đăng nhập thành công với quyền User", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                }
                else -> {
                    // Sai thông tin đăng nhập
                    Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}