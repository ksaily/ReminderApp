package com.example.reminder

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.reminder.databinding.ActivityEditReminderBinding
import java.util.*

@Suppress("DEPRECATION")
class EditReminderActivity : AppCompatActivity() {
    //Add new reminders, executed when the plus button on menu screen is pressed

    private lateinit var binding: ActivityEditReminderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditReminderBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //get the id of reminder to update
        val uid = applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference),
                Context.MODE_PRIVATE
        ).getInt("UID", 0)

        //get the information of reminder
        val name2 = applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference),
                Context.MODE_PRIVATE
        ).getString("name","")
        val date2 = applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference),
                Context.MODE_PRIVATE
        ).getString("date","")
        val time2 = applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference),
                Context.MODE_PRIVATE
        ).getString("time","")
        //show these to the user
        binding.reminderEditInfo.setText(name2)
        binding.reminderEditDate.setText(date2)
        binding.reminderEditTime.setText(time2)


        binding.btnAcceptEdit.setOnClickListener {
            //validate entry values here
            if (binding.reminderEditDate.text.isEmpty() || binding.reminderEditInfo.text.isEmpty() ||
                binding.reminderEditTime.text.isEmpty()) {
                    Toast.makeText(
                        applicationContext,
                        "None of the fields should be empty and date should be in dd.mm.yyyy format",
                        Toast.LENGTH_SHORT
                    ).show()
                return@setOnClickListener
            }
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "com.example.reminder"
            ).build()

            val reminderInfo = ReminderInfo(
                uid,
                name = binding.reminderEditInfo.text.toString(),
                date = binding.reminderEditDate.text.toString(),
                time = binding.reminderEditTime.text.toString(),
                creation_time = Calendar.getInstance().timeInMillis,
                reminder_seen = "no"
            )

            //convert date  string value to Date format using dd.mm.yyyy
            // here it is asummed that date is in dd.mm.yyyy
            val dateparts = reminderInfo.date.split(".").toTypedArray()
            val calender = GregorianCalendar(
                dateparts[2].toInt(),
                dateparts[1].toInt() - 1,
                dateparts[0].toInt()
            )

            AsyncTask.execute {
                //update reminder to room database and the notification
                ReminderHistory.cancelReminder(
                        applicationContext,
                        uid
                )
                val message =
                        "Reminder! ${reminderInfo.name}, on ${reminderInfo.date} at ${reminderInfo.time}"
                ReminderHistory.setRemnder(
                        applicationContext,
                        uid,
                        calender.timeInMillis,
                        message
                )
                val db = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    "com.example.reminder"
                ).build()
                val uuid = db.reminderDao().update(reminderInfo)
                db.close()
            }
            finish()
            //return to menu screen
            val menuActivityIntent = Intent(applicationContext, MenuActivity::class.java)
            startActivity(menuActivityIntent)
        }

    }

}



