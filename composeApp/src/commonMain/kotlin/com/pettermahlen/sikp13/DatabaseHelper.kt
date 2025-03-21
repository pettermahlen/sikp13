package com.pettermahlen.sikp13

import app.cash.sqldelight.db.SqlDriver
import com.pettermahlen.sikp13.db.AppDatabase

interface DatabaseHelper {
    // Empty interface - we might add more functionality later
}

class DatabaseHelperImpl(
    private val appDatabase: AppDatabase
) : DatabaseHelper {
    // Empty implementation - we might add more functionality later
}

interface DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): AppDatabase {
    val driver = driverFactory.createDriver()
    return AppDatabase.invoke(driver)
} 