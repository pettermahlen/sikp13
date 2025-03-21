package com.pettermahlen.sikp13

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.pettermahlen.sikp13.db.AppDatabase

class IosDriverFactory : DriverFactory {
    override fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = AppDatabase.Schema,
            name = "app.db"
        )
    }
} 