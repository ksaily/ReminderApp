package com.example.reminder

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
import com.example.reminder.AppDatabase
import com.example.reminder.ReminderInfo
import com.example.reminder.databinding.ActivityMenuBinding
import com.example.reminder.databinding.ActivityReminderListviewBinding


@Suppress("DEPRECATION")
class MenuActivity : AppCompatActivity() {
    private lateinit var binding1: ActivityMenuBinding
    private lateinit var listView: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding1= ActivityMenuBinding.inflate(layoutInflater)
        val view =binding1.root
        setContentView(view)

        listView = binding1.menuListView

        //update userInterface
        refreshListView()

        binding1.addReminder.setOnClickListener {
            var reminderIntent = Intent(applicationContext, ReminderActivity::class.java)
            startActivity(reminderIntent)
        }

        binding1.profileButton.setOnClickListener {
            var profileIntent = Intent(applicationContext,ProfileScreen::class.java)
            startActivity(profileIntent)
        }

            listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, id ->
                //retrieve selected Item

                val selectedReminder = listView.adapter.getItem(position) as ReminderInfo
                val message =
                    "Do you want to delete ${selectedReminder.name} payment, at ${selectedReminder.time} on ${selectedReminder.date} ?"

                // Show AlertDialog to delete the reminder
                val builder = AlertDialog.Builder(this@MenuActivity)
                builder.setTitle("Delete reminder?")
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
                    .setNegativeButton("Cancel") { dialog, _ ->
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
            var refreshTask = LoadReminderInfoEntries()
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


       /* companion object {
            val List = mutableListOf<ReminderInfo>()
        }*/
    }