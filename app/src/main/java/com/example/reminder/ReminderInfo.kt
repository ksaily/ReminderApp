package com.example.reminder

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

    @Entity(tableName = "reminderInfo")
    data class ReminderInfo(
        @PrimaryKey(autoGenerate = true) var uid: Int?,
        @ColumnInfo(name = "name") var name: String,
        @ColumnInfo(name = "date") var date: String,
        /*@ColumnInfo(name = "hours") var hours: Int,
        @ColumnInfo(name = "minutes") var minutes: Int,*/
        @ColumnInfo(name = "key") var key: String="",
        @ColumnInfo(name = "lat") var lat: Double=0.0,
        @ColumnInfo(name = "lon") var lon: Double=0.0,
        @ColumnInfo(name = "within_area") var within_area: Boolean=false,
        @ColumnInfo(name = "creation_time") var creation_time: Long,
        @ColumnInfo(name = "reminder_seen") var reminder_seen: String
    )