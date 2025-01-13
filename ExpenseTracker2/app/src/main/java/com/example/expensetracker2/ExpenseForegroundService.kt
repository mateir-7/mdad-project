package com.example.expensetracker2
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ExpenseForegroundService : Service() {
    private val channelId = "expense_channel"
    private val notificationId = 1

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        updateNotification(2000.0) // Initially show the full budget
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val remainingBudget = intent?.getDoubleExtra("remainingBudget", 2000.0) ?: 2000.0
        updateNotification(remainingBudget)
        return START_STICKY
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId,
            "Expense Notifications",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    @SuppressLint("ForegroundServiceType")
    private fun updateNotification(remainingBudget: Double) {
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Expense Tracker")
            .setContentText("Remaining Budget: $${"%.2f".format(remainingBudget)}")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(notificationId, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
