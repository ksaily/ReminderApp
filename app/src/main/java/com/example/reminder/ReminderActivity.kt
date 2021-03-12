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
import android.util.Log
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
    private var key=""
    private var lat=0.0
    private var lon=0.0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityReminderBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //hide keyboard when the dateTextBox is clicked
        binding.reminderAddDate.inputType = InputType.TYPE_NULL
        binding.reminderAddDate.isClickable = true
        //show date and time dialog

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

        if (intent.hasExtra("key")) {
            lat = intent.getDoubleExtra("lat", 0.0)
            lon = intent.getDoubleExtra("lon", 0.0)
            key = intent.getStringExtra("key")!!
            onLocationSet(key!!, lat, lon)
            Log.d("ReminderActivity", "saved lon: $lon, lat: $lat, key: $key")

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


            /*if (key != "") {
                val database = Firebase.database(getString(R.string.firebase_database))
                val reference = database.getReference("reminders")
                reference.child(key!!).get().addOnSuccessListener {
                    val reminder = it.getValue<Reminder>()
                    lon = reminder!!.lon
                    lat = reminder!!.lat
                    Log.d("ReminderActivity", "saved lon: $lng, lat: $lat")
                    onLocationSet(key, lng, lat)
                }.addOnFailureListener {
                    Log.e("firebase", "Error getting data", it)
                }
            }*/

            val reminderInfo = ReminderInfo(
                null,
                name = binding.reminderAddInfo.text.toString(),
                date = binding.reminderAddDate.text.toString(),
                key,
                lat,
                lon,
                creation_time = Calendar.getInstance().timeInMillis,
            )

            val reminderCalendar2 = GregorianCalendar.getInstance()
            val dateFormat =
                "dd.MM.yyyy HH:mm" // change this format to dd.MM.yyyy if you have not time in your date.
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
                    val dateparts =
                        reminderInfo.date.split(" ").toTypedArray()[0].split(".").toTypedArray()
                    val timeparts =
                        reminderInfo.date.split(" ").toTypedArray()[1].split(":").toTypedArray()

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

                //save reminder to room database
                val db = databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    "com.example.reminder"
                ).build()
                val uuid = db.reminderDao().insert(reminderInfo).toInt()
                db.close()


                if ((reminderCalendar.timeInMillis > Calendar.getInstance().timeInMillis) && (reminderInfo.key != "")) {
                    // event happens in the future set reminder
                    val message =
                        "Reminder! ${reminderInfo.name}, Date (and time): ${reminderInfo.date}, Location $lat, $lon reached"
                    ReminderHistory.setReminderWithWorkManager(
                        applicationContext,
                        uuid,
                        reminderCalendar.timeInMillis,
                        key,
                        lat,
                        lon,
                        message
                    )
                } else if (reminderCalendar.timeInMillis > Calendar.getInstance().timeInMillis) {
                    val message =
                        "Reminder! ${reminderInfo.name}, Date (and time): ${reminderInfo.date}"

                    ReminderHistory.setReminderWithWorkManager(
                        applicationContext,
                        uuid,
                        reminderCalendar.timeInMillis,
                        key,
                        lat,
                        lon,
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

            println("Reminder added: "+ reminderInfo)

            startActivity(Intent(applicationContext, MenuActivity::class.java))

        }    //return to menu screen
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

}

