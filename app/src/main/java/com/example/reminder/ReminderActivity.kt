package com.example.reminder

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import com.example.reminder.AppDatabase
import com.example.reminder.ReminderInfo
import com.example.reminder.databinding.ActivityReminderBinding
import java.util.*

@Suppress("DEPRECATION")
class ReminderActivity : AppCompatActivity() {
    //Add new reminders, executed when the plus button on menu screen is pressed

    private lateinit var binding: ActivityReminderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReminderBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnAccept.setOnClickListener {
            //validate entry values here
            if (binding.reminderAddDate.text.isEmpty() || binding.reminderAddInfo.text.isEmpty() ||
                    binding.reminderAddTime.text.isEmpty()) {
                Toast.makeText(
                        applicationContext,
                        "None of the fields should be empty and date should be in dd.mm.yyyy format",
                        Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val reminderInfo = ReminderInfo(
                    null,
                    name = binding.reminderAddInfo.text.toString(),
                    date = binding.reminderAddDate.text.toString(),
                    time = binding.reminderAddTime.text.toString()
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
                //save reminder to room database
                val db = databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java,
                        "com.example.reminder"
                ).build()
                val uuid = db.reminderDao().insert(reminderInfo).toInt()
                db.close()
            }
            finish()

            //return to menu screen
            val menuActivityIntent = Intent(applicationContext, MenuActivity::class.java)
            startActivity(menuActivityIntent)
        }

        }

    }

