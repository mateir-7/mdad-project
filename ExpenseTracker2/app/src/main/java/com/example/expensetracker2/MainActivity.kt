package com.example.expensetracker2

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: ExpenseDatabaseHelper
    private lateinit var expenseListView: ListView
    private lateinit var remainingBudgetTextView: TextView
    private val ADD_EXPENSE_REQUEST_CODE = 1
    private var monthlyBudget = 4000.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = ExpenseDatabaseHelper(this)
        expenseListView = findViewById(R.id.expenseListView)
        remainingBudgetTextView = findViewById(R.id.remainingBudgetTextView)

        val addExpenseButton: Button = findViewById(R.id.addExpenseButton)
        addExpenseButton.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            startActivity(intent)
        }

        val deleteAllButton: Button = findViewById(R.id.deleteAllButton)
        deleteAllButton.setOnClickListener {
            val success = dbHelper.deleteAllExpenses()
            if (success) {
                Toast.makeText(this, "All expenses deleted", Toast.LENGTH_SHORT).show()
                loadExpenses()
                updateRemainingBudget()
            } else {
                Toast.makeText(this, "Failed to delete expenses", Toast.LENGTH_SHORT).show()
            }
        }

        // Initial data load
        loadExpenses()
        updateRemainingBudget()

        // Setup background alert
        setupBackgroundAlert()
    }

    override fun onResume() {
        super.onResume()
        // Reload expenses and update remaining budget when activity is resumed
        loadExpenses()
        updateRemainingBudget()
    }

    private fun loadExpenses() {
        val expenses = dbHelper.getAllExpenses()
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            expenses.map { "${it.title} - $${it.amount} on ${it.date}" }
        )
        expenseListView.adapter = adapter
    }

    private fun updateRemainingBudget() {
        val totalExpenses = dbHelper.getTotalExpenses()
        val remainingBudget = monthlyBudget - totalExpenses
        remainingBudgetTextView.text = "Remaining Budget: $${String.format("%.2f", remainingBudget)}"
    }

    private fun setupBackgroundAlert() {
        val intent = Intent(this, ExpenseAlertReceiver::class.java) // Updated receiver name
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerTime = System.currentTimeMillis() + (5 * 1000)

        // Set exact alarm
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )

        // Log to confirm alarm setup
        Log.d("BackgroundService", "Alarm for background service set successfully for 1-minute interval")
    }
}
