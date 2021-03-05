package com.example.reminder

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.text.InputType
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.room.Room.databaseBuilder
import androidx.work.*
import androidx.work.Worker
import com.example.reminder.databinding.ActivityReminderBinding
import com.google.android.gms.maps.LocationSource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.properties.Delegates


@Suppress("DEPRECATION")
class ReminderActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {
    //Add new reminders, executed when the plus button on menu screen is pressed

    private lateinit var binding: ActivityReminderBinding
    private lateinit var reminderCalendar: Calendar
    var key: String=""
    var lat: Double = 0.0
    var lng: Double = 0.0
    private lateinit var reminder: Reminder


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityReminderBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //hide keyboard when the dateTextBox is clicked
        binding.reminderAddDate.inputType = InputType.TYPE_NULL
        binding.reminderAddDate.isClickable = true
        //show date and time dialog

        reminder = Reminder(key, lat, lng)

        binding.reminderAddDate.setOnClickListener {
            reminderCalendar = GregorianCalendar.getInstance()
            DatePickerDialog(
                    this,
                    this,
                    reminderCalendar.get(Calendar.YEAR),
                    reminderCalendar.get(Calendar.MONTH),
                    reminderCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.MapIcon.setOnClickListener {
            var mapsIntent = Intent(applicationContext, MapsActivity::class.java)
            startActivity(mapsIntent)
        }

        binding.btnAccept.setOnClickListener {
            //validate entry values here
            if (binding.reminderAddDate.text.isEmpty()) {
                Toast.makeText(
                        applicationContext,
                        "Date field should not be empty",
                        Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val reminderInfo = ReminderInfo(
                    null,
                    name = binding.reminderAddInfo.text.toString(),
                    date = binding.reminderAddDate.text.toString(),
                    key = reminder.key,
                    lat = reminder.lat,
                    lon = reminder.lon,
                    creation_time = Calendar.getInstance().timeInMillis,
                    reminder_seen = "no"
            )

            val reminderCalendar2 = GregorianCalendar.getInstance()
            val dateFormat = "dd.MM.yyyy HH:mm" // change this format to dd.MM.yyyy if you have not time in your date.
            // a better way of handling dates but requires API version 26 (Build.VERSION_CODES.O)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val formatter = DateTimeFormatter.ofPattern(dateFormat)
                val date = LocalDateTime.parse(reminderInfo.date, formatter)

                reminderCalendar2.set(Calendar.YEAR, date.year)
                reminderCalendar2.set(Calendar.MONTH, date.monthValue - 1)
                reminderCalendar2.set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
                reminderCalendar2.set(Calendar.HOUR_OF_DAY, date.hour)
                reminderCalendar2.set(Calendar.MINUTE, date.minute)

            } else {
                if (dateFormat.contains(":")) {
                    // if your date contains hours and minutes and its in the format dd.mm.yyyy HH:mm
                    val dateparts = reminderInfo.date.split(" ").toTypedArray()[0].split(".").toTypedArray()
                    val timeparts = reminderInfo.date.split(" ").toTypedArray()[1].split(":").toTypedArray()

                    reminderCalendar2.set(Calendar.YEAR, dateparts[2].toInt())
                    reminderCalendar2.set(Calendar.MONTH, dateparts[1].toInt() - 1)
                    reminderCalendar2.set(Calendar.DAY_OF_MONTH, dateparts[0].toInt())
                    val hour = reminderCalendar2.set(Calendar.HOUR_OF_DAY, timeparts[0].toInt())
                    val minutes = reminderCalendar2.set(Calendar.MINUTE, timeparts[1].toInt())


                } else {
                    //no time part
                    //convert date  string value to Date format using dd.mm.yyyy
                    // here it is assumed that date is in dd.mm.yyyy
                    val dateparts = reminderInfo.date.split(".").toTypedArray()
                    reminderCalendar2.set(Calendar.YEAR, dateparts[2].toInt())
                    reminderCalendar2.set(Calendar.MONTH, dateparts[1].toInt() - 1)
                    reminderCalendar2.set(Calendar.DAY_OF_MONTH, dateparts[0].toInt())
                }
            }


            AsyncTask.execute {
                onLocationSet(key, lat, lng)
                //save reminder to room database
                val db = databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java,
                        "com.example.reminder"
                ).build()
                val uuid = db.reminderDao().insert(reminderInfo).toInt()
                db.close()

                if (reminderCalendar.timeInMillis > Calendar.getInstance().timeInMillis && lat != 0.0 && lng != 0.0) {
                    // event happens in the future set reminder
                    val message =
                            "Reminder! ${reminderInfo.name}, Date (and time): ${reminderInfo.date}," +
                                    "Location $lat, $lng reached"
                    ReminderHistory.setReminderWithWorkManager(
                            applicationContext,
                            uuid,
                            reminderCalendar.timeInMillis,
                            lat,
                            lng,
                            reminderInfo.within_area,
                            message
                    )
                }
                    else if (reminderCalendar.timeInMillis > Calendar.getInstance().timeInMillis) {
                        val message =
                                "Reminder! ${reminderInfo.name}, Date (and time): ${reminderInfo.date}"

                        ReminderHistory.setReminderWithWorkManager(
                                applicationContext,
                                uuid,
                                reminderCalendar.timeInMillis,
                                lat,
                                lng,
                                reminderInfo.within_area,
                                message
                        )
                    }


                }

            if (reminderCalendar.timeInMillis > Calendar.getInstance().timeInMillis) {
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

    override fun onDateSet(
            dailogView: DatePicker?,
            selectedYear: Int,
            selectedMonth: Int,
            selectedDayOfMonth: Int
    ) {
        reminderCalendar.set(Calendar.YEAR, selectedYear)
        reminderCalendar.set(Calendar.MONTH, selectedMonth)
        reminderCalendar.set(Calendar.DAY_OF_MONTH, selectedDayOfMonth)
        val simplDateFormat = SimpleDateFormat("dd.MM.yyyy")
        binding.reminderAddDate.setText(simplDateFormat.format(reminderCalendar.time))

        // if you want to show time picker after the date
        TimePickerDialog(
                this,
                this,
                reminderCalendar.get(Calendar.HOUR_OF_DAY),
                reminderCalendar.get(Calendar.MINUTE),
                true
        ).show()
    }

    override fun onTimeSet(view: TimePicker?, selectedhourOfDay: Int, selectedMinute: Int) {
        reminderCalendar.set(Calendar.HOUR_OF_DAY, selectedhourOfDay)
        reminderCalendar.set(Calendar.MINUTE, selectedMinute)
        val simleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
        binding.reminderAddDate.setText(simleDateFormat.format(reminderCalendar.time))
    }

    fun onLocationSet(key: String, lat: Double, lon: Double) {
        if (key != "") {
            val string1 = lat.toString()
            val string2 = lon.toString()
            binding.Location.setText(string1 + string2)
        }
    }

    override fun onResume() {
        super.onResume()
        val key = applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference),
                Context.MODE_PRIVATE
        ).getString("KEY", "")

        //get the information of reminder
        val lat = applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference),
                Context.MODE_PRIVATE
        ).getString("lat", "")
        val lon = applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference),
                Context.MODE_PRIVATE
        ).getString("lon", "")
        reminder = Reminder(key!!, lat!!.toDouble(), lon!!.toDouble())
        onLocationSet(reminder.key, reminder.lat, reminder.lon)
    }

}

