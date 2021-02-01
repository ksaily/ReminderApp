package com.example.reminder

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

class ReminderInfo {


    @Entity(tableName = "reminderInfo")
    data class ReminderInfo(
        @PrimaryKey(autoGenerate = true) var uid: Int?,
        @ColumnInfo(name = "info") var info: String,
        @ColumnInfo(name = "date") var date: String,
        @ColumnInfo(name = "time") var time: String
    )
}