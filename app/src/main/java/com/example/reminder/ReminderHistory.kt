package com.example.reminder

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.room.Room
import com.example.reminder.databinding.ActivityReminderHistoryBinding
import kotlin.random.Random

class ReminderHistory : AppCompatActivity() {
    private lateinit var binding: ActivityReminderHistoryBinding
    private lateinit var listView: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityReminderHistoryBinding.inflate(layoutInflater)
        val view =binding.root
        setContentView(view)

        listView = binding.menuListView

        //update userInterface
        refreshListView()

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, id ->
            //retrieve selected reminder

            val selectedReminder = listView.adapter.getItem(position) as ReminderInfo
            val message =
                    "Do you want to delete or edit ${selectedReminder.name} reminder, at ${selectedReminder.time} on ${selectedReminder.date} ?"
            // Show AlertDialog to delete the reminder
            val builder = AlertDialog.Builder(this@ReminderHistory)
            builder.setTitle("Delete or edit reminder?")
                    .setMessage(message)
                    .setPositiveButton("Delete") { _, _ ->
                        // Update UI


                        //delete from database and delete reminder
                        AsyncTask.execute {
                            cancelReminder(
                                    applicationContext,
                                    selectedReminder.uid!!
                            )
                            val db = Room.databaseBuilder(
                                    applicationContext,
                                    AppDatabase::class.java,
                                    "com.example.reminder"
                            )
                                    .build()
                            db.reminderDao().delete(selectedReminder.uid!!)
                        }


                        //refresh reminder list
                        refreshListView()
                    }
                    //set the edit button
                    .setNegativeButton("Edit") { _, _ ->
                        //save the information of selected reminder to shared preferences
                        applicationContext.getSharedPreferences(
                                getString(R.string.sharedPreference),
                                Context.MODE_PRIVATE
                        ).edit().putInt("UID", selectedReminder.uid!!).apply()
                        applicationContext.getSharedPreferences(
                                getString(R.string.sharedPreference),
                                Context.MODE_PRIVATE
                        ).edit().putString("name", selectedReminder.name).apply()
                        applicationContext.getSharedPreferences(
                                getString(R.string.sharedPreference),
                                Context.MODE_PRIVATE
                        ).edit().putString("time", selectedReminder.time).apply()
                        applicationContext.getSharedPreferences(
                                getString(R.string.sharedPreference),
                                Context.MODE_PRIVATE
                        ).edit().putString("date", selectedReminder.date).apply()

                        val reminderEditIntent = Intent(applicationContext, EditReminderActivity::class.java)
                        startActivity(reminderEditIntent)
                    }
                    .setNeutralButton("Cancel") { dialog, _ ->
                        // Do nothing
                        dialog.dismiss()
                    }
                    .show()

        }
    }

    override fun onResume() {
        super.onResume()
        refreshListView()
    }

    @Suppress("DEPRECATION")
    private fun refreshListView() {
        val refreshTask = LoadReminderInfoEntries()
        refreshTask.execute()
    }

    @Suppress("DEPRECATION")
    inner class LoadReminderInfoEntries : AsyncTask<String?, String?, List<ReminderInfo>>() {
        override fun doInBackground(vararg params: String?): List<ReminderInfo> {
            val db = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    "com.example.reminder"
            )
                    .build()
            val reminderInfos = db.reminderDao().getReminderInfos()
            db.close()
            return reminderInfos
        }

        @Suppress("DEPRECATION")
        override fun onPostExecute(reminderInfos: List<ReminderInfo>?) {
            super.onPostExecute(reminderInfos)
            if (reminderInfos != null) {
                if (reminderInfos.isNotEmpty()) {
                    val adaptor = ReminderAdaptor(applicationContext, reminderInfos)
                    listView.adapter = adaptor
                } else {
                    listView.adapter = null
                    val text = "You have no reminders"
                    val duration = Toast.LENGTH_LONG

                    val toast = Toast.makeText(applicationContext, text, duration)
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                    toast.show()


                }
            }
        }

    }

    companion object {
         //val List = mutableListOf<ReminderInfo>()
         @SuppressLint("ServiceCast")
         fun showNofitication(context: Context, message: String) {

             val CHANNEL_ID = "REMINDER_APP_NOTIFICATION_CHANNEL"
             var notificationId = Random.nextInt(10, 1000) + 5
             // notificationId += Random(notificationId).nextInt(1, 500)

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

             notificationManager.notify(notificationId, notificationBuilder.build())

         }

        @SuppressLint("ServiceCast")
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

        fun cancelReminder(context: Context, pendingIntentId: Int) {

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
        }
        fun getListItem(context: Context, reminderInf: List<ReminderInfo>?, uid: Int): Any {
                return reminderInf!![uid]
        }
    }
}