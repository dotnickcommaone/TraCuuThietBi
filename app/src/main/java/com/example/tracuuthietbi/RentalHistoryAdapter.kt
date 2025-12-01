package com.example.tracuuthietbi

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class RentalHistoryAdapter(context: Context, records: List<RentalRecord>) : ArrayAdapter<RentalRecord>(context, 0, records) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val record = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_rental_history, parent, false)

        val usernameTextView = view.findViewById<TextView>(R.id.text_view_history_username)
        val rentalDateTextView = view.findViewById<TextView>(R.id.text_view_history_rental_date)
        val returnDateTextView = view.findViewById<TextView>(R.id.text_view_history_return_date)

        if (record != null) {
            usernameTextView.text = "Người thuê: ${record.username}"
            rentalDateTextView.text = "Ngày thuê: ${record.rentalDate}"
            if (record.returnDate != null) {
                returnDateTextView.text = "Ngày trả: ${record.returnDate}"
                returnDateTextView.visibility = View.VISIBLE
            } else {
                returnDateTextView.visibility = View.GONE
            }
        }

        return view
    }
}