package com.example.reminder

import androidx.room.Database
import androidx.room.RoomDatabase

class AppDatabase {

    @Database(entities = arrayOf(ReminderInfo::class), version = 1)
    abstract class AppDatabase : RoomDatabase() {
        abstract fun reminderDao(): ReminderDao
    }
}