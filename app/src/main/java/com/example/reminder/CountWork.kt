package com.example.reminder

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.*
import androidx.work.Worker
import java.util.*
import java.util.concurrent.TimeUnit

class CountWork(appContext: Context, workerParams: WorkerParameters): Worker(appContext, workerParams) {

    override fun doWork(): Result {

        // Calculate time until reminder
        val ReminderTime = inputData.getLong("time", 0)
        val currentTime = Calendar.getInstance().timeInMillis
        val timeDifference = ReminderTime - currentTime

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }

@RequiresApi(Build.VERSION_CODES.O)
val uploadWorkRequest: WorkRequest =
    OneTimeWorkRequestBuilder<CountWork>()
        .build()

val WorkManage = WorkManager
.getInstance(appContext)
.enqueue(uploadWorkRequest)

}

