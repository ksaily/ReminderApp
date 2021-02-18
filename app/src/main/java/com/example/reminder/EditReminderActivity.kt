package com.example.reminder

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import android.view.LayoutInflater
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

            //get the id of room entity to update
            val uid = applicationContext.getSharedPreferences(
                    getString(R.string.sharedPreference),
                    Context.MODE_PRIVATE
            ).getInt("UID", 0)

            val reminderInfo = ReminderInfo(
                    uid,
                    name = binding.reminderEditInfo.text.toString(),
                    date = binding.reminderEditDate.text.toString(),
                    time = binding.reminderEditTime.text.toString()
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
                //update reminder to room database
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
