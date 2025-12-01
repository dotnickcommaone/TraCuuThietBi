package com.example.tracuuthietbi

import java.io.Serializable

data class Device(
    val id: Int = 0,
    val name: String,
    val type: String,
    val isAvailable: Boolean,
    val quantity: Int,
    val imageUri: String? = null,
    val rented_by_user: String? = null // Thêm người dùng đang thuê
) : Serializable