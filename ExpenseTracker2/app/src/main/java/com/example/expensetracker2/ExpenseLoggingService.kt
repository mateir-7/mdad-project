package com.example.expensetracker2

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class ExpenseLoggingService : Service() {

    private var isRunning = false
    private lateinit var loggingThread: Thread

    override fun onCreate() {
        super.onCreate()
        isRunning = true

        // Initialize the logging thread
        loggingThread = Thread {
            while (isRunning) {
                try {
                    // Log to Logcat
                    Log.d("ExpenseLoggingService", "An expense has been added!")

                    // Sleep for 15 seconds
                    Thread.sleep(15000)
                } catch (e: InterruptedException) {
                    Log.e("ExpenseLoggingService", "Thread interrupted: ${e.message}")
                    break
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Start the thread when the service is started
        if (!loggingThread.isAlive) {
            loggingThread.start()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        loggingThread.interrupt() // Interrupt the thread if the service is stopped
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // This service doesn't support binding
    }
}
