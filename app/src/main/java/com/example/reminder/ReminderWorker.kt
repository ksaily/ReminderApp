package com.example.reminder

import android.content.Context
import android.os.Build
import android.provider.Settings.Global.getString
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.*
import androidx.work.Worker
import com.example.reminder.R.string.firebase_database
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
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

        if (key == "") {
            //if there is no location requirement
            ReminderHistory.showNotification(applicationContext, text!!, key, lat, lon)
        }

        if (key != "") {
            //if there is a location requirement, wait for geofence enter(ReminderReceiver)
            //set reminder seen to true
            val database = Firebase.database("https://reminder-app-306517-default-rtdb.firebaseio.com/")
            val reference = database.getReference("reminders")
            val reminder = reference.child(key!!).child("reminder_seen").setValue(true)
            /* val reminderListener = object : ValueEventListener {
               override fun onDataChange(snapshot: DataSnapshot) {
                    val reminder = reference.child(key!!)
                        Log.d("ReminderWorker", "Reminder seen")
                        reminder.child("reminder_seen").setValue(true)
                    }

                override fun onCancelled(error: DatabaseError) {
                    println("reminder:onCancelled: ${error.details}")
                }

            }
            val child = reference.child(key!!)
            child.addValueEventListener(reminderListener)*/
        }

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}

