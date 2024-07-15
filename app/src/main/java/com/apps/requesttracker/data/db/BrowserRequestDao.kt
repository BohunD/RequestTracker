package com.apps.requesttracker.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface QueryDao {
    @Query("SELECT * FROM query_table ORDER BY requestDateTime DESC")
    fun getAllQueries(): LiveData<List<QueryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(query: QueryEntity)

    @Delete
    suspend fun delete(query: QueryEntity)

    @Query("SELECT * FROM query_table WHERE websiteLink = :url LIMIT 1")
    suspend fun getQueryByUrl(url: String): QueryEntity?

    @Query("SELECT * FROM query_table ORDER BY requestDateTime DESC LIMIT 1")
    suspend fun getLastQuery(): QueryEntity?

    @Query("UPDATE query_table SET websiteLink = :websiteLink WHERE id = :id")
    suspend fun updateWebsiteLink(id: Int, websiteLink: String)
}
