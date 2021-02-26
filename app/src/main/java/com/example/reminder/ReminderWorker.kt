package com.example.reminder

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.*
import androidx.work.Worker
import java.util.*

class ReminderWorker(appContext: Context, workerParams: WorkerParameters):
        Worker(appContext, workerParams) {

    override fun doWork(): Result {

        // Do work
        val text = inputData.getString("message") // this comes from the reminder parameters
        ReminderHistory.showNofitication(applicationContext, text!!)

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}

