package com.example.reminder

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.*
import androidx.work.Worker
import java.util.*

class ReminderWorker(appContext: Context, workerParams: WorkerParameters):
        Worker(appContext, workerParams) {

    private lateinit var reminderCalendar: Calendar
    private lateinit var reminderInfo: ReminderInfo
    override fun doWork(): Result {

        // Do work
        val text = inputData.getString("message") // this comes from the reminder parameters
        val date = inputData.getString("date")
        val lat = inputData.getDouble("lat", 0.0)
        val lon = inputData.getDouble("lon", 0.0)
        val within_range = reminderInfo.within_area

        reminderCalendar = GregorianCalendar.getInstance()

        val dateparts = date!!.split(" ").toTypedArray()[0].split(".").toTypedArray()
        reminderCalendar.set(Calendar.YEAR, dateparts[2].toInt())
        reminderCalendar.set(Calendar.MONTH, dateparts[1].toInt() - 1)
        reminderCalendar.set(Calendar.DAY_OF_MONTH, dateparts[0].toInt())

        if (lat == 0.0 && lon == 0.0) {
            ReminderHistory.showNofitication(applicationContext, text!!)
        }

        if (date.contains(":")) {
            //if the date contains time reminder
            val timeparts = date.split(" ").toTypedArray()[1].split(":").toTypedArray()
            reminderCalendar.set(Calendar.DAY_OF_MONTH, dateparts[0].toInt())
            reminderCalendar.set(Calendar.HOUR_OF_DAY, timeparts[0].toInt())
            reminderCalendar.set(Calendar.MINUTE, timeparts[1].toInt())
        }

        if (within_range) {
            ReminderHistory.showNofitication(applicationContext, text!!)
            //if time is right and location within range
        }

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}

