package com.example.tracuuthietbi

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class DeviceAdapter(context: Context, devices: List<Device>) : ArrayAdapter<Device>(context, 0, devices) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val device = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_device, parent, false)

        val imageViewDevice = view.findViewById<ImageView>(R.id.image_view_device_item)
        val textViewDeviceName = view.findViewById<TextView>(R.id.text_view_device_name)
        val imageViewDot = view.findViewById<ImageView>(R.id.image_view_dot)

        textViewDeviceName.text = device?.name

        if (device?.isAvailable == true) {
            imageViewDot.setImageResource(R.drawable.dot_green)
        } else {
            imageViewDot.setImageResource(R.drawable.dot_red)
        }

        device?.imageUri?.let {
            val imageUri = Uri.parse(it)
            Glide.with(context)
                .load(imageUri)
                .placeholder(R.drawable.ic_launcher_background) // Ảnh tạm thời trong khi tải
                .error(R.drawable.ic_launcher_foreground) // Ảnh khi có lỗi
                .into(imageViewDevice)
        } ?: run {
            // Nếu không có URI, hiển thị ảnh mặc định
            imageViewDevice.setImageResource(R.mipmap.ic_launcher)
        }

        return view
    }
}