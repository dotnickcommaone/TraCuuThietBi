package com.example.tracuuthietbi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        val editTextUsername = findViewById<EditText>(R.id.edit_text_username)
        val editTextPassword = findViewById<EditText>(R.id.edit_text_password)
        val buttonLogin = findViewById<Button>(R.id.button_login)
        val textViewRegister = findViewById<TextView>(R.id.text_view_register)
        val textViewForgotPassword = findViewById<TextView>(R.id.text_view_forgot_password)

        buttonLogin.setOnClickListener {
            val username = editTextUsername.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên đăng nhập và mật khẩu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            when {
                username == "admin" && password == "admin" -> {
                    Toast.makeText(this, "Đăng nhập thành công với quyền Admin", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AdminActivity::class.java))
                }
                dbHelper.checkUser(username, password) -> {
                    Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, UserActivity::class.java)
                    intent.putExtra("USERNAME", username) // **Gửi username đi**
                    startActivity(intent)
                }
                else -> {
                    Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show()
                }
            }
        }

        textViewRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        textViewForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }
}