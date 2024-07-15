package com.apps.requesttracker.domain.repository

import androidx.lifecycle.LiveData
import com.apps.requesttracker.data.db.QueryEntity

interface QueryRepository {
    val allQueries: LiveData<List<QueryEntity>>

    suspend fun insert(query: QueryEntity)

    suspend fun getLastQuery(): QueryEntity?

    suspend fun updateWebsiteLink(id: Int, websiteLink: String)

    suspend fun delete(query: QueryEntity)
}
