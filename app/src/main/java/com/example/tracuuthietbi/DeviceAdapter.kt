package com.example.tracuuthietbi

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class DeviceAdapter(context: Context, devices: List<Device>) : ArrayAdapter<Device>(context, 0, devices) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val device = getItem(position)
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_device, parent, false)
        }

        val textViewDeviceName = view!!.findViewById<TextView>(R.id.text_view_device_name)
        val imageViewDot = view.findViewById<ImageView>(R.id.image_view_dot)

        textViewDeviceName.text = device?.name

        if (device?.isAvailable == true) {
            imageViewDot.setImageResource(R.drawable.dot_green)
        } else {
            imageViewDot.setImageResource(R.drawable.dot_red)
        }

        return view
    }
}