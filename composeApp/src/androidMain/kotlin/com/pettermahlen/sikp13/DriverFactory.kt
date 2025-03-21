package com.pettermahlen.sikp13

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.pettermahlen.sikp13.db.AppDatabase

class AndroidDriverFactory(private val context: Context) : DriverFactory {
    override fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = AppDatabase.Schema,
            context = context,
            name = "app.db"
        )
    }
} 