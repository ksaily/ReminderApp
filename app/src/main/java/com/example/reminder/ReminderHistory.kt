package com.example.reminder

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import java.util.concurrent.TimeUnit
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.work.*
import com.example.reminder.databinding.ActivityReminderHistoryBinding
import com.example.reminder.databinding.ActivityReminderListviewBinding
import com.example.reminder.databinding.ActivityReminderListviewBinding.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

class ReminderHistory : AppCompatActivity() {

    private lateinit var binding: ActivityReminderHistoryBinding
    private lateinit var listView: ListView
    private lateinit var reminderParameters: WorkerParameters
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    companion object {
        var virtual_lat = 0.0
        var virtual_lon = 0.0
        //val List = mutableListOf<ReminderInfo>()
        @SuppressLint("ServiceCast")
        fun showNotification(context: Context, message: String, key: String, lat: Double, lon: Double) {

            val CHANNEL_ID = "REMINDER_APP_NOTIFICATION_CHANNEL"
            var notificationId = Random.nextInt(10, 1000) + 5
            notificationId += Random(notificationId).nextInt(1, 500)

            var notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_reminder_icon)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setGroup(CHANNEL_ID)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Notification chancel needed since Android 8
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = context.getString(R.string.app_name)
                }
                notificationManager.createNotificationChannel(channel)
            }

            if (key != "") {
                val results = floatArrayOf()
                Location.distanceBetween(virtual_lat, virtual_lon, lat, lon, results)
                if (results[0] <= GEOFENCE_RADIUS) {
                    notificationManager.notify(notificationId, notificationBuilder.build())
                }
            }

            notificationManager.notify(notificationId, notificationBuilder.build())
        }

        fun setReminderWithWorkManager(
                context: Context,
                uid: Int,
                timeInMillis: Long,
                key: String,
                location_lat: Double,
                location_lon: Double,
                message: String
        ) {
            val reminderParameters = Data.Builder()
                    .putString("message", message)
                    .putInt("uid", uid)
                    .putString("key", key)
                    .putDouble("lat", location_lat)
                    .putDouble("lon", location_lon)
                    .build()

            // get minutes from now until reminder
            var minutesFromNow = 0L
            if (timeInMillis > System.currentTimeMillis())
                minutesFromNow = timeInMillis - System.currentTimeMillis()

            val reminderRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                    .setInputData(reminderParameters)
                    .setInitialDelay(minutesFromNow, TimeUnit.MILLISECONDS)
                    .build()

            WorkManager.getInstance(context).enqueue(reminderRequest)
            //val isFinished = WorkManager.getInstance(context).getWorkInfoById(reminderRequest.id)
            //       .isDone
            //if (isFinished.equals(true)) {


        }



        fun setRemnder(context: Context, uid: Int, timeInMillis: Long, message: String) {
            val intent = Intent(context, ReminderReceiver::class.java)
            intent.putExtra("uid", uid)
            intent.putExtra("message", message)

            // create a pending intent to a  future action with a uniquie request code i.e uid
            val pendingIntent =
                    PendingIntent.getBroadcast(context, uid, intent, PendingIntent.FLAG_ONE_SHOT)

            //create a service to monitor and execute the fure action.
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(AlarmManager.RTC, timeInMillis, pendingIntent)

        }

        fun cancelReminder(context: Context, pendingIntentId: Int, key: String) {

            val intent = Intent(context, ReminderReceiver::class.java)
            val pendingIntent =
                    PendingIntent.getBroadcast(
                            context,
                            pendingIntentId,
                            intent,
                            PendingIntent.FLAG_ONE_SHOT
                    )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            val database = Firebase.database("https://reminder-app-306517-default-rtdb.firebaseio.com/")
            val reference = database.getReference("reminders")
            reference.child("reminders").child(key).removeValue()
        }
    }
}
