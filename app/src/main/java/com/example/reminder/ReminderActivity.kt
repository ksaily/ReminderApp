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
            if (binding.reminderAddDate.text.isEmpty() /*|| binding.reminderAddInfo.text.isEmpty() ||
                    binding.reminderAddTime.text.isEmpty()*/) {
                Toast.makeText(
                        applicationContext,
                        "Info should not be empty",
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
            val timeparts = reminderInfo.time.split(":").toTypedArray()
            val reminderCalendar = GregorianCalendar(
                    dateparts[2].toInt(),
                    dateparts[1].toInt() - 1,
                    dateparts[0].toInt(),
                    timeparts[0].toInt(),
                    timeparts[1].toInt()
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

                if (reminderCalendar.timeInMillis > Calendar.getInstance().timeInMillis) {
                    // event happens in the future set reminder
                    val message =
                            "Reminder! ${reminderInfo.name}, on ${reminderInfo.date} at ${reminderInfo.time}"
                    ReminderHistory.setRemnder(
                            applicationContext,
                            uuid,
                            reminderCalendar.timeInMillis,
                            message
                    )
                }
            }

            if(reminderCalendar.timeInMillis>Calendar.getInstance().timeInMillis){
                Toast.makeText(
                        applicationContext,
                        "Reminder for future saved.",
                        Toast.LENGTH_SHORT
                ).show()
            }

            finish()

                //return to menu screen
            val menuActivityIntent = Intent(applicationContext, MenuActivity::class.java)
            startActivity(menuActivityIntent)
        }

    }

}

