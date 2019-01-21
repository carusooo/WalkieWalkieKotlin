package com.example.macarus0.walkiewalkie.data

import android.content.Context
import androidx.room.Room


object WalkieDatabaseProvider {

    private const val dbName = "WalkieDatabase"

    private lateinit var sDb: WalkieDatabase

    fun getDatabase(context: Context): WalkieDatabase {
        if (sDb == null) {
            sDb = createDatabase(context)
        }
        return sDb
    }

    private fun createDatabase(context: Context): WalkieDatabase {
        return Room.databaseBuilder<WalkieDatabase>(context.applicationContext, WalkieDatabase::class.java, dbName).build()
    }

}
