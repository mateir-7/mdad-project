package com.example.expensetracker2

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var amountInput: EditText
    private lateinit var dateInput: EditText
    private lateinit var saveButton: Button

    private val ALERT_THRESHOLD = 1000.0 // Threshold for background alert

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        titleInput = findViewById(R.id.titleInput)
        amountInput = findViewById(R.id.amountInput)
        dateInput = findViewById(R.id.dateInput)
        saveButton = findViewById(R.id.saveButton)

        saveButton.setOnClickListener {
            val title = titleInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()
            val date = dateInput.text.toString()

            if (title.isNotBlank() && amount != null && date.isNotBlank()) {
                val dbHelper = ExpenseDatabaseHelper(this)
                val success = dbHelper.insertExpense(title, amount, date)

                if (success) {
                    Toast.makeText(this, "Expense saved successfully", Toast.LENGTH_SHORT).show()

                    // Trigger a background alert if total expenses exceed the threshold
                    val totalExpenses = dbHelper.getTotalExpenses()
                    if (totalExpenses >= ALERT_THRESHOLD) {
                        setupBackgroundAlert()
                    }

                    // Show notification if individual expense exceeds $2000
                    if (amount > 2000) {
                        showNotification("Warning", "Large Transaction Detected")
                    }

                    // Start the ExpenseLoggingService
                    val serviceIntent = Intent(this, ExpenseLoggingService::class.java)
                    startService(serviceIntent)

                    finish() // Close activity and return to the previous screen
                } else {
                    Toast.makeText(this, "Failed to save expense", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "expense_tracker_channel"
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Expense Tracker Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(this).notify(1, notification)
    }

    private fun setupBackgroundAlert() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlertReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE // Ensure FLAG_IMMUTABLE for Android 31+
        )

        // Set an immediate alarm to trigger the receiver
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            pendingIntent
        )
    }
}
