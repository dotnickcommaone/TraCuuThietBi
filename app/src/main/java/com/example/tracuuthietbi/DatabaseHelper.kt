package com.example.tracuuthietbi

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 5
        private const val DATABASE_NAME = "DeviceManager.db"

        private const val TABLE_DEVICES = "devices"
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_TYPE = "type"
        private const val KEY_IS_AVAILABLE = "is_available"
        private const val KEY_QUANTITY = "quantity"
        private const val KEY_IMAGE_URI = "image_uri"
        private const val KEY_RENTED_BY = "rented_by_user"

        private const val TABLE_USERS = "users"
        private const val KEY_USER_ID = "id"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"

        private const val TABLE_RENTAL_HISTORY = "rental_history"
        private const val KEY_RECORD_ID = "id"
        private const val KEY_RECORD_DEVICE_ID = "device_id"
        private const val KEY_RECORD_USERNAME = "username"
        private const val KEY_RENTAL_DATE = "rental_date"
        private const val KEY_RETURN_DATE = "return_date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createDevicesTable = """CREATE TABLE $TABLE_DEVICES (
            $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT, $KEY_NAME TEXT, $KEY_TYPE TEXT, 
            $KEY_IS_AVAILABLE INTEGER, $KEY_QUANTITY INTEGER, $KEY_IMAGE_URI TEXT, $KEY_RENTED_BY TEXT
        )"""
        db?.execSQL(createDevicesTable)

        val createUsersTable = """CREATE TABLE $TABLE_USERS (
            $KEY_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, $KEY_USERNAME TEXT UNIQUE, $KEY_PASSWORD TEXT
        )"""
        db?.execSQL(createUsersTable)

        val createRentalHistoryTable = """CREATE TABLE $TABLE_RENTAL_HISTORY (
            $KEY_RECORD_ID INTEGER PRIMARY KEY AUTOINCREMENT, $KEY_RECORD_DEVICE_ID INTEGER, 
            $KEY_RECORD_USERNAME TEXT, $KEY_RENTAL_DATE TEXT, $KEY_RETURN_DATE TEXT
        )"""
        db?.execSQL(createRentalHistoryTable)

        addDummyData(db)
    }

    private fun addDummyData(db: SQLiteDatabase?) {
        val userValues = ContentValues().apply {
            put(KEY_USERNAME, "user")
            put(KEY_PASSWORD, "user")
        }
        db?.insert(TABLE_USERS, null, userValues)

        val device1 = ContentValues().apply {
            put(KEY_NAME, "MacBook Pro 16")
            put(KEY_TYPE, "Laptop")
            put(KEY_IS_AVAILABLE, 0)
            put(KEY_QUANTITY, 0)
            put(KEY_RENTED_BY, "user")
        }
        db?.insert(TABLE_DEVICES, null, device1)

        val device2 = ContentValues().apply {
            put(KEY_NAME, "Dell XPS 15")
            put(KEY_TYPE, "Laptop")
            put(KEY_IS_AVAILABLE, 1)
            put(KEY_QUANTITY, 5)
        }
        db?.insert(TABLE_DEVICES, null, device2)

        val device3 = ContentValues().apply {
            put(KEY_NAME, "iPad Pro")
            put(KEY_TYPE, "Tablet")
            put(KEY_IS_AVAILABLE, 1)
            put(KEY_QUANTITY, 10)
        }
        db?.insert(TABLE_DEVICES, null, device3)

        val history1 = ContentValues().apply {
            put(KEY_RECORD_DEVICE_ID, 1)
            put(KEY_RECORD_USERNAME, "user")
            put(KEY_RENTAL_DATE, "01/06/2024 10:00")
        }
        db?.insert(TABLE_RENTAL_HISTORY, null, history1)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 5) {
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_DEVICES")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_RENTAL_HISTORY")
            onCreate(db)
        }
    }

    private fun createContentValues(device: Device): ContentValues {
        return ContentValues().apply {
            put(KEY_NAME, device.name)
            put(KEY_TYPE, device.type)
            put(KEY_IS_AVAILABLE, if (device.isAvailable) 1 else 0)
            put(KEY_QUANTITY, device.quantity)
            put(KEY_IMAGE_URI, device.imageUri)
            put(KEY_RENTED_BY, device.rented_by_user)
        }
    }

    fun addDevice(device: Device) {
        writableDatabase.insert(TABLE_DEVICES, null, createContentValues(device))
    }

    fun updateDevice(device: Device): Int {
        return writableDatabase.update(TABLE_DEVICES, createContentValues(device), "$KEY_ID = ?", arrayOf(device.id.toString()))
    }

    fun deleteDevice(id: Int) {
        writableDatabase.delete(TABLE_DEVICES, "$KEY_ID = ?", arrayOf(id.toString()))
    }

    @SuppressLint("Range")
    private fun cursorToDevice(cursor: Cursor): Device {
        return Device(
            id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
            name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)),
            type = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TYPE)),
            isAvailable = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_AVAILABLE)) == 1,
            quantity = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_QUANTITY)),
            imageUri = cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE_URI)),
            rented_by_user = cursor.getString(cursor.getColumnIndexOrThrow(KEY_RENTED_BY))
        )
    }

    fun getDevice(id: Int): Device? {
        readableDatabase.query(TABLE_DEVICES, null, "$KEY_ID=?", arrayOf(id.toString()), null, null, null).use { cursor ->
            return if (cursor.moveToFirst()) cursorToDevice(cursor) else null
        }
    }

    fun getAllDevices(): List<Device> {
        val deviceList = mutableListOf<Device>()
        readableDatabase.rawQuery("SELECT * FROM $TABLE_DEVICES", null).use { cursor ->
            while (cursor.moveToNext()) {
                deviceList.add(cursorToDevice(cursor))
            }
        }
        return deviceList
    }

    fun getDevicesByType(type: String): List<Device> {
        val deviceList = mutableListOf<Device>()
        val query = "SELECT * FROM $TABLE_DEVICES WHERE $KEY_TYPE = ?"
        readableDatabase.rawQuery(query, arrayOf(type)).use { cursor ->
            while (cursor.moveToNext()) {
                deviceList.add(cursorToDevice(cursor))
            }
        }
        return deviceList
    }

    fun isUserRentingDeviceFromCategory(username: String, deviceType: String): Boolean {
        val query = """
            SELECT h.$KEY_RECORD_ID
            FROM $TABLE_RENTAL_HISTORY h
            INNER JOIN $TABLE_DEVICES d ON h.$KEY_RECORD_DEVICE_ID = d.$KEY_ID
            WHERE h.$KEY_RECORD_USERNAME = ?
            AND d.$KEY_TYPE = ?
            AND h.$KEY_RETURN_DATE IS NULL
        """
        readableDatabase.rawQuery(query, arrayOf(username, deviceType)).use { cursor ->
            return cursor.count > 0
        }
    }

    fun addUser(username: String, password: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_USERNAME, username)
        values.put(KEY_PASSWORD, password)
        return db.insert(TABLE_USERS, null, values)
    }

    fun checkUser(username: String, password: String): Boolean {
        readableDatabase.query(TABLE_USERS, arrayOf(KEY_USER_ID), "$KEY_USERNAME = ? AND $KEY_PASSWORD = ?", arrayOf(username, password), null, null, null).use { cursor ->
            return cursor.count > 0
        }
    }

    fun checkUserExists(username: String): Boolean {
        readableDatabase.query(TABLE_USERS, arrayOf(KEY_USER_ID), "$KEY_USERNAME = ?", arrayOf(username), null, null, null).use { cursor ->
            return cursor.count > 0
        }
    }

    fun updateUserPassword(username: String, newPassword: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_PASSWORD, newPassword)
        return db.update(TABLE_USERS, values, "$KEY_USERNAME = ?", arrayOf(username))
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }

    fun addRentalRecord(deviceId: Int, username: String) {
        val values = ContentValues().apply {
            put(KEY_RECORD_DEVICE_ID, deviceId)
            put(KEY_RECORD_USERNAME, username)
            put(KEY_RENTAL_DATE, getCurrentDate())
        }
        writableDatabase.insert(TABLE_RENTAL_HISTORY, null, values)
    }

    fun updateReturnDate(deviceId: Int, username: String) {
        val values = ContentValues().apply {
            put(KEY_RETURN_DATE, getCurrentDate())
        }
        writableDatabase.update(TABLE_RENTAL_HISTORY, values, "$KEY_RECORD_DEVICE_ID = ? AND $KEY_RECORD_USERNAME = ? AND $KEY_RETURN_DATE IS NULL", arrayOf(deviceId.toString(), username))
    }

    @SuppressLint("Range")
    fun getRentalHistory(deviceId: Int): List<RentalRecord> {
        val historyList = mutableListOf<RentalRecord>()
        readableDatabase.query(TABLE_RENTAL_HISTORY, null, "$KEY_RECORD_DEVICE_ID = ?", arrayOf(deviceId.toString()), null, null, "$KEY_RECORD_ID DESC").use { cursor ->
            while (cursor.moveToNext()) {
                historyList.add(RentalRecord(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RECORD_ID)),
                    deviceId = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RECORD_DEVICE_ID)),
                    username = cursor.getString(cursor.getColumnIndexOrThrow(KEY_RECORD_USERNAME)),
                    rentalDate = cursor.getString(cursor.getColumnIndexOrThrow(KEY_RENTAL_DATE)),
                    returnDate = cursor.getString(cursor.getColumnIndexOrThrow(KEY_RETURN_DATE))
                ))
            }
        }
        return historyList
    }
}