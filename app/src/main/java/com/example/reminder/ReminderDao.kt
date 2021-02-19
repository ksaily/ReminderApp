package com.example.reminder

import androidx.room.*
import java.util.*

@Dao
interface ReminderDao {
    @Transaction
    @Insert
    fun insert(reminderInfo: ReminderInfo): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    //Update the current reminder if information is changed
    fun update(reminderInfo: ReminderInfo)

    @Query("DELETE FROM reminderInfo WHERE uid = :id")
    fun delete(id: Int)

    @Query("SELECT * FROM reminderInfo")
    fun getReminderInfos(): List<ReminderInfo>

    /*@Query("SELECT name, date, time FROM reminderInfo WHERE uid = :id")
    fun getReminderInfo(id: Int)*/

}