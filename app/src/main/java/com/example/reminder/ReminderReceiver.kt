package com.example.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        // Retrieve data from intent
        val uid = intent?.getIntExtra("uid", 0)
        val text = intent?.getStringExtra("message")


        ReminderHistory.showNofitication(context!!,text!!)
    }
}