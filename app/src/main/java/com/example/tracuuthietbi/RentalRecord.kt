package com.example.tracuuthietbi

data class RentalRecord(
    val id: Int,
    val deviceId: Int,
    val username: String,
    val rentalDate: String,
    val returnDate: String?
)