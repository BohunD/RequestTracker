package com.apps.requesttracker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [QueryEntity::class], version = 1, exportSchema = false)
abstract class QueryDatabase : RoomDatabase() {
    abstract fun queryDao(): QueryDao
}
