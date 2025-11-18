package com.example.tracuuthietbi

data class Device(
    val id: Int = 0,
    val name: String,
    val type: String,
    val isAvailable: Boolean,
    val quantity: Int
)
