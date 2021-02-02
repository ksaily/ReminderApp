package com.example.reminder

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface UserDao {
    @Transaction
    @Insert
    fun insert(userInfo: UserInfo): Long

    @Query("DELETE FROM userInfo WHERE uid = :id")
    fun delete(id: Int)

    @Query("SELECT * FROM userInfo")
    fun getReminderInfos(): List<UserInfo>
}