package com.example.tracuuthietbi

import java.io.Serializable

data class Device(
    val id: Int = 0,
    val name: String,
    val type: String,
    val isAvailable: Boolean,
    val quantity: Int,
    val imageUri: String? = null // Thêm trường để lưu URI ảnh
) : Serializable // Implement Serializable để có thể truyền đối tượng qua Intent nếu cần