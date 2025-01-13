package com.example.expensetracker2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Toast.makeText(context, "Please be more mindful with spending money", Toast.LENGTH_LONG).show()
    }
}
