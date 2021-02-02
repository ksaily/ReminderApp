package com.example.reminder

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

    @Entity(tableName = "userInfo")
    data class UserInfo(
            @PrimaryKey(autoGenerate = true) var uid: Int?,
            @ColumnInfo(name = "username") var username: String,
            @ColumnInfo(name = "password") var password: String,
            @ColumnInfo(name = "Login Status") var loginstatus: Int
    )