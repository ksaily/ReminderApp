package com.example.reminder

import android.content.Context
import android.os.AsyncTask
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.ListView
import android.widget.Toast
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.room.Room
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room.databaseBuilder
import com.example.reminder.databinding.ActivityMenuBinding


@Suppress("DEPRECATION")
class MenuActivity : AppCompatActivity() {
    //Menu stores the reminders in a list view
    private lateinit var binding: ActivityMenuBinding
    private lateinit var listView: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMenuBinding.inflate(layoutInflater)
        val view =binding.root
        setContentView(view)

        listView = binding.menuListView

        //update userInterface
        refreshListView()

        binding.addReminder.setOnClickListener {
            //If the plus button is clicked, move to add new reminders in ReminderActivity
            val reminderIntent = Intent(applicationContext, ReminderActivity::class.java)
            startActivity(reminderIntent)
        }

        binding.profileButton.setOnClickListener {
            //If the profile button is clicked, move to ProfileScreen
            val profileIntent = Intent(applicationContext,ProfileScreen::class.java)
            startActivity(profileIntent)
        }
        binding.FutureReminders.setOnClickListener {
            val futureReminderIntent = Intent(applicationContext, ReminderHistory::class.java)
            startActivity(futureReminderIntent)
        }

            listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, id ->
                //retrieve selected reminder

                val selectedReminder = listView.adapter.getItem(position) as ReminderInfo
                val message =
                    "Do you want to delete or edit ${selectedReminder.name} reminder, at ${selectedReminder.time} on ${selectedReminder.date} ?"
                // Show AlertDialog to delete the reminder
                val builder = AlertDialog.Builder(this@MenuActivity)
                builder.setTitle("Delete or edit reminder?")
                    .setMessage(message)
                    .setPositiveButton("Delete") { _, _ ->
                        // Update UI


                        //delete from database
                        AsyncTask.execute {
                            val db = databaseBuilder(
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
                        val text = "You have no reminders that have occurred"
                        val duration = Toast.LENGTH_LONG

                        val toast = Toast.makeText(applicationContext, text, duration)
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                        toast.show()


                    }
                }
            }

        }
}