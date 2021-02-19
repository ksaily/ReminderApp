package com.example.reminder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.reminder.ReminderInfo
import com.example.reminder.databinding.ActivityMenuBinding
import com.example.reminder.databinding.ActivityReminderListviewBinding



class ReminderAdaptor(context: Context, private  val list: List<ReminderInfo>) : BaseAdapter() {
    // Adaptor for the list view in menu
    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, container: ViewGroup?): View? {
        var rowBinding = ActivityReminderListviewBinding.inflate(inflater, container, false)

                //set reminder info values to the list item
                rowBinding.ReminderInfo.text = list[position].name
                rowBinding.ReminderDate.text = list[position].date
                rowBinding.ReminderTime.text = list[position].time
            return  rowBinding.root
    }
    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

}