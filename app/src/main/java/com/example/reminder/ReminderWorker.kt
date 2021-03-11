package com.example.reminder

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.*
import androidx.work.Worker
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class ReminderWorker(appContext: Context, workerParams: WorkerParameters):
        Worker(appContext, workerParams) {

    private lateinit var reminderCalendar: Calendar
    private lateinit var reminderInfo: ReminderInfo
    override fun doWork(): Result {

        // Do work
        val text = inputData.getString("message") // this comes from the reminder parameters
        val date = inputData.getString("date")
        val key = inputData.getString("key")
        val lat = inputData.getDouble("lat", 0.0)
        val lon = inputData.getDouble("lon", 0.0)

        /*reminderCalendar = GregorianCalendar.getInstance()

        val dateparts = date!!.split(" ").toTypedArray()[0].split(".").toTypedArray()
        reminderCalendar.set(Calendar.YEAR, dateparts[2].toInt())
        reminderCalendar.set(Calendar.MONTH, dateparts[1].toInt() - 1)
        reminderCalendar.set(Calendar.DAY_OF_MONTH, dateparts[0].toInt())*/

        if (key == "") {
            //if there is no location requirement
            ReminderHistory.showNotification(applicationContext, text!!)
        }

        if (key != "") {
            //if there is a location requirement, wait for geofence enter(ReminderReceiver)
            val firebase = Firebase.database
            val reference = firebase.getReference("reminders")
            //set reminder seen to true
            reference.child(key!!).child("reminder_seen").setValue(true)
        }

        /*if (date.contains(":")) {
            //if the date contains time reminder
            val timeparts = date.split(" ").toTypedArray()[1].split(":").toTypedArray()
            reminderCalendar.set(Calendar.DAY_OF_MONTH, dateparts[0].toInt())
            reminderCalendar.set(Calendar.HOUR_OF_DAY, timeparts[0].toInt())
            reminderCalendar.set(Calendar.MINUTE, timeparts[1].toInt())
        }*/

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}

