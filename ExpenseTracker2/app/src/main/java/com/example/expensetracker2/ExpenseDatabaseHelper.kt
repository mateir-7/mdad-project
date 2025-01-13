package com.example.expensetracker2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Expense(val id: Int, val title: String, val amount: Double, val date: String)

class ExpenseDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE expenses (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT,
                amount REAL,
                date TEXT
            )
        """
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS expenses")
        onCreate(db)
    }

    fun insertExpense(title: String, amount: Double, date: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", title)
            put("amount", amount)
            put("date", date)
        }
        val result = db.insert("expenses", null, values)
        db.close()
        return result != -1L
    }

    fun getAllExpenses(): List<Expense> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM expenses", null)
        val expenses = mutableListOf<Expense>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                expenses.add(Expense(id, title, amount, date))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return expenses
    }

    fun getTotalExpenses(): Double {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT SUM(amount) FROM expenses", null)
        var total = 0.0
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        cursor.close()
        db.close()
        return total
    }

    fun deleteAllExpenses(): Boolean {
        val db = writableDatabase
        val result = db.delete("expenses", null, null)
        db.close()
        return result > 0
    }

    companion object {
        private const val DATABASE_NAME = "expenses.db"
        private const val DATABASE_VERSION = 1
    }
}
