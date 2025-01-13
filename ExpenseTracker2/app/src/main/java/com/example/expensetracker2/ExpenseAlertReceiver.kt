package com.example.expensetracker2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat

class ExpenseAlertReceiver : BroadcastReceiver() {
    private val channelId = "expense_alert_channel"

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val totalExpenses = getTotalExpensesFromDatabase(it)

            println("ExpenseAlertReceiver triggered. Total expenses: $totalExpenses")

            if (totalExpenses >= 4000.0) {
                sendNotification(it, totalExpenses)
            }
        }
    }

    private fun sendNotification(context: Context, totalExpenses: Double) {
        createNotificationChannel(context)

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Spending Alert")
            .setContentText("You have no money left. Total spent: $$totalExpenses")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(2, notification)

        // Debug log
        println("Notification sent: Total spent = $totalExpenses")
    }

    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            channelId,
            "Spending Alerts",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun getTotalExpensesFromDatabase(context: Context): Double {
        val dbHelper = ExpenseDatabaseHelper(context)
        return dbHelper.getTotalExpenses()
    }
}
