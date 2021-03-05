package com.example.reminder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.reminder.ReminderInfo
import com.example.reminder.databinding.ActivityMenuBinding
import com.example.reminder.databinding.ActivityReminderListviewBinding
import java.util.*


class ReminderAdaptor(context: Context, private val list: List<ReminderInfo>?) : BaseAdapter() {
    // Adaptor for the list view in menu
    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private lateinit var reminderCalendar: Calendar

    override fun getView(position: Int, convertView: View?, container: ViewGroup?): View? {
        var rowBinding = ActivityReminderListviewBinding.inflate(inflater, container, false)
        reminderCalendar = GregorianCalendar.getInstance()

        val dateparts = list!![position].date.split(" ").toTypedArray()[0].split(".").toTypedArray()
        reminderCalendar.set(Calendar.YEAR, dateparts[2].toInt())
        reminderCalendar.set(Calendar.MONTH, dateparts[1].toInt() - 1)
        reminderCalendar.set(Calendar.DAY_OF_MONTH, dateparts[0].toInt())

        if (list!![position].date.contains(":")) {
            //if the date contains time reminder
            //val dateparts = list[position].date.split(" ").toTypedArray()[0].split(".").toTypedArray()
            val timeparts = list[position].date.split(" ").toTypedArray()[1].split(":").toTypedArray()
            /*reminderCalendar.set(Calendar.YEAR, dateparts[2].toInt())
            reminderCalendar.set(Calendar.MONTH, dateparts[1].toInt() - 1)*/
            reminderCalendar.set(Calendar.DAY_OF_MONTH, dateparts[0].toInt())
            reminderCalendar.set(Calendar.HOUR_OF_DAY, timeparts[0].toInt())
            reminderCalendar.set(Calendar.MINUTE, timeparts[1].toInt())

            if (reminderCalendar.timeInMillis > System.currentTimeMillis()) {
                //if time is in the future, dont add these reminders to rowbinding
            }
            else {
                //if the reminder time has passed
                rowBinding.ReminderInfo.text = list!![position].name
                rowBinding.ReminderDate.text = list[position].date
            }
        }
            if (list!![position].lat != 0.0 && list!![position].lon != 0.0) {
                rowBinding.ReminderInfo.text = list[position].name
                rowBinding.ReminderDate.text = list[position].date
                rowBinding.LocationText.text = (list!![position].lat.toString() + list!![position].lon.toString())

            }
            else {
                //set reminder info values to the list item
                rowBinding.ReminderInfo.text = list!![position].name
                rowBinding.ReminderDate.text = list!![position].date
            }
        return rowBinding.root
    }



        override fun getItem(position: Int): Any {
            return list!![position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return list!!.size
        }

}

