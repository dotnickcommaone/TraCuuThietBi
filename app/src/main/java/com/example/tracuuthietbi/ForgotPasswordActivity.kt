package com.example.tracuuthietbi

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        dbHelper = DatabaseHelper(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Quên mật khẩu"

        val editTextUsername = findViewById<EditText>(R.id.edit_text_username_forgot)
        val editTextNewPassword = findViewById<EditText>(R.id.edit_text_new_password)
        val buttonResetPassword = findViewById<Button>(R.id.button_reset_password)

        buttonResetPassword.setOnClickListener {
            val username = editTextUsername.text.toString().trim()
            val newPassword = editTextNewPassword.text.toString().trim()

            if (username.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dbHelper.checkUserExists(username)) {
                val result = dbHelper.updateUserPassword(username, newPassword)
                if (result > 0) {
                    Toast.makeText(this, "Cập nhật mật khẩu thành công", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Lỗi cập nhật mật khẩu", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Tên đăng nhập không tồn tại", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}