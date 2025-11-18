package com.example.tracuuthietbi

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "DeviceManager.db"
        private const val TABLE_DEVICES = "devices"
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_TYPE = "type"
        private const val KEY_IS_AVAILABLE = "is_available"
        private const val KEY_QUANTITY = "quantity"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """CREATE TABLE $TABLE_DEVICES (
            $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $KEY_NAME TEXT,
            $KEY_TYPE TEXT,
            $KEY_IS_AVAILABLE INTEGER,
            $KEY_QUANTITY INTEGER
        )"""
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_DEVICES")
        onCreate(db)
    }

    private fun createContentValues(device: Device): ContentValues {
        return ContentValues().apply {
            put(KEY_NAME, device.name)
            put(KEY_TYPE, device.type)
            put(KEY_IS_AVAILABLE, if (device.isAvailable) 1 else 0)
            put(KEY_QUANTITY, device.quantity)
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
            quantity = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_QUANTITY))
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
}