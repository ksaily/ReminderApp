package com.example.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.room.Room
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.lang.reflect.Array.get

class ReminderReceiver : BroadcastReceiver(){
    lateinit var key: String
    lateinit var lat: String
    lateinit var lon: String
    lateinit var text: String
    lateinit var reminderInfo: ReminderInfo
    override fun onReceive(context: Context?, intent: Intent?) {

        // Retrieve data from intent
        if (intent != null) {
            val uid = intent?.getIntExtra("uid", 0)
            val text = intent?.getStringExtra("message")
            val lat = intent?.getStringExtra("lat")
            val lon = intent?.getStringExtra("lon")
        }
        if (context != null) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)
            val geofencingTransition = geofencingEvent.geofenceTransition

            if (geofencingTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofencingTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

                val firebase = Firebase.database
                val reference = firebase.getReference("reminders")
                val reminderListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val reminder = snapshot.getValue<Reminder>()
                        if (reminder != null) {
                            reminderInfo.within_area = true
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println("reminder:onCancelled: ${error.details}")
                    }

                }
                val child = reference.child(key)
                child.addValueEventListener(reminderListener)

                // remove geofence
                val triggeringGeofences = geofencingEvent.triggeringGeofences
                MapsActivity.removeGeofences(context, triggeringGeofences)
            }
        }
        //ReminderHistory.showNofitication(context!!,text!!)

    }
}