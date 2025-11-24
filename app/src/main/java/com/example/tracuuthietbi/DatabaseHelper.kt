package com.example.tracuuthietbi

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 2 // Tăng phiên bản DB
        private const val DATABASE_NAME = "DeviceManager.db"

        // Bảng thiết bị
        private const val TABLE_DEVICES = "devices"
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_TYPE = "type"
        private const val KEY_IS_AVAILABLE = "is_available"
        private const val KEY_QUANTITY = "quantity"

        // Bảng người dùng
        private const val TABLE_USERS = "users"
        private const val KEY_USER_ID = "id"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createDevicesTable = """CREATE TABLE $TABLE_DEVICES (
            $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $KEY_NAME TEXT,
            $KEY_TYPE TEXT,
            $KEY_IS_AVAILABLE INTEGER,
            $KEY_QUANTITY INTEGER
        )"""
        db?.execSQL(createDevicesTable)

        val createUsersTable = """CREATE TABLE $TABLE_USERS (
            $KEY_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $KEY_USERNAME TEXT UNIQUE,
            $KEY_PASSWORD TEXT
        )"""
        db?.execSQL(createUsersTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_DEVICES")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
            onCreate(db)
        }
    }

    // --- Device Functions ---
    // (Các hàm của thiết bị giữ nguyên ở đây)
    private fun createContentValues(device: Device): ContentValues { return ContentValues().apply { put(KEY_NAME, device.name); put(KEY_TYPE, device.type); put(KEY_IS_AVAILABLE, if (device.isAvailable) 1 else 0); put(KEY_QUANTITY, device.quantity) } }
    fun addDevice(device: Device) { writableDatabase.insert(TABLE_DEVICES, null, createContentValues(device)) }
    fun updateDevice(device: Device): Int { return writableDatabase.update(TABLE_DEVICES, createContentValues(device), "$KEY_ID = ?", arrayOf(device.id.toString())) }
    fun deleteDevice(id: Int) { writableDatabase.delete(TABLE_DEVICES, "$KEY_ID = ?", arrayOf(id.toString())) }
    @SuppressLint("Range") private fun cursorToDevice(cursor: Cursor): Device { return Device(id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)), name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)), type = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TYPE)), isAvailable = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_AVAILABLE)) == 1, quantity = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_QUANTITY))) }
    fun getDevice(id: Int): Device? { readableDatabase.query(TABLE_DEVICES, null, "$KEY_ID=?", arrayOf(id.toString()), null, null, null).use { cursor -> return if (cursor.moveToFirst()) cursorToDevice(cursor) else null } }
    fun getAllDevices(): List<Device> { val deviceList = mutableListOf<Device>(); readableDatabase.rawQuery("SELECT * FROM $TABLE_DEVICES", null).use { cursor -> while (cursor.moveToNext()) { deviceList.add(cursorToDevice(cursor)) } }; return deviceList }

    // --- User Functions ---
    fun addUser(username: String, password: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_USERNAME, username)
        values.put(KEY_PASSWORD, password) // Lưu ý: Nên mã hóa mật khẩu trong ứng dụng thực tế
        return db.insert(TABLE_USERS, null, values)
    }

    fun checkUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val columns = arrayOf(KEY_USER_ID)
        val selection = "$KEY_USERNAME = ? AND $KEY_PASSWORD = ?"
        val selectionArgs = arrayOf(username, password)
        readableDatabase.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null).use { cursor ->
            return cursor.count > 0
        }
    }

    fun checkUserExists(username: String): Boolean {
        val db = this.readableDatabase
        val columns = arrayOf(KEY_USER_ID)
        val selection = "$KEY_USERNAME = ?"
        val selectionArgs = arrayOf(username)
        readableDatabase.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null).use { cursor ->
            return cursor.count > 0
        }
    }

    fun updateUserPassword(username: String, newPassword: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_PASSWORD, newPassword)
        return db.update(TABLE_USERS, values, "$KEY_USERNAME = ?", arrayOf(username))
    }
}