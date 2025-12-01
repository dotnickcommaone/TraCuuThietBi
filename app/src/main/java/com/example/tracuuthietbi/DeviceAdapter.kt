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
        val textViewRentedBy = view.findViewById<TextView>(R.id.text_view_rented_by)
        val imageViewDot = view.findViewById<ImageView>(R.id.image_view_dot)

        textViewDeviceName.text = device?.name

        if (device?.rented_by_user != null) {
            textViewRentedBy.text = "Người thuê: ${device.rented_by_user}"
            textViewRentedBy.visibility = View.VISIBLE
        } else {
            textViewRentedBy.visibility = View.GONE
        }

        if (device?.isAvailable == true) {
            imageViewDot.setImageResource(R.drawable.dot_green)
        } else {
            imageViewDot.setImageResource(R.drawable.dot_red)
        }

        device?.imageUri?.let {
            val imageUri = Uri.parse(it)
            Glide.with(context)
                .load(imageUri)
                .placeholder(R.mipmap.ic_launcher) 
                .error(R.mipmap.ic_launcher_round) 
                .into(imageViewDevice)
        } ?: run {
            imageViewDevice.setImageResource(R.mipmap.ic_launcher)
        }

        return view
    }
}