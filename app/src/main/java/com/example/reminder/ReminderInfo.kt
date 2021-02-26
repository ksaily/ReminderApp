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
        @ColumnInfo(name = "minutes") var minutes: Int,
        @ColumnInfo(name = "location_x") var location_x: String,
        @ColumnInfo(name = "location_y") var location_y: String,
        @ColumnInfo(name = "creator_id") var creator_id: String,*/
        @ColumnInfo(name = "creation_time") var creation_time: Long,
        @ColumnInfo(name = "reminder_seen") var reminder_seen: String
    )