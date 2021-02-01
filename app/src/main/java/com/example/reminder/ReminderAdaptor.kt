package com.example.reminder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.reminder.ReminderInfo

abstract class ReminderAdaptor(context: Context, private  val list:List<ReminderInfo>): BaseAdapter() {}

    /*private val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val row = inflater.inflate(R.layout.activity_reminder_listview, parent, false

        //set payment info values to the list item
        row.txtInfo.text=list[position].info
        row.txtDate.text=list[position].date
        row.txtTime.text=list[position].time
        return  row
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
*/