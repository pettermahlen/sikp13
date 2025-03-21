package com.pettermahlen.sikp13

import android.app.Application
import com.pettermahlen.sikp13.db.AppDatabase

class MainApplication : Application() {
    
    companion object {
        lateinit var databaseHelper: DatabaseHelper
    }
    
    override fun onCreate() {
        super.onCreate()
        
        val driverFactory = AndroidDriverFactory(applicationContext)
        val appDatabase = createDatabase(driverFactory)
        databaseHelper = DatabaseHelperImpl(appDatabase)
        
        // Initialize the players database
        DatabaseProvider.initialize(driverFactory)
    }
} 