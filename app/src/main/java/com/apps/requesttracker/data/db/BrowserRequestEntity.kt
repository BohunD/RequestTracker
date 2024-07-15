package com.apps.requesttracker.data.db
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "query_table")
data class QueryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val requestText: String,
    val requestDateTime: Long,
    val websiteLink: String?,
)