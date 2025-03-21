package com.pettermahlen.sikp13

import app.cash.sqldelight.db.SqlDriver
import com.pettermahlen.sikp13.db.AppDatabase

object DatabaseProvider {
    private var database: AppDatabase? = null
    private var playersDb: SqlDelightPlayersDb? = null

    fun initialize(driverFactory: DriverFactory) {
        if (database == null) {
            val driver = driverFactory.createDriver()
            // The schema is automatically created by the driver
            database = AppDatabase(driver)
            playersDb = SqlDelightPlayersDb(database!!)
        }
    }

    fun getPlayersDb(): PlayersDb {
        return playersDb ?: throw IllegalStateException("Database not initialized. Call initialize() first.")
    }
} 