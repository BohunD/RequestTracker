package com.apps.requesttracker.data.repository

import com.apps.requesttracker.data.db.QueryDao
import com.apps.requesttracker.data.db.QueryEntity
import com.apps.requesttracker.domain.repository.QueryRepository
import javax.inject.Inject

class QueryRepositoryImpl @Inject constructor(private val queryDao: QueryDao): QueryRepository {
    override val allQueries = queryDao.getAllQueries()

    override suspend fun insert(query: QueryEntity) {
        queryDao.insert(query)
    }

    override suspend fun delete(query: QueryEntity) {
        queryDao.delete(query)
    }

    override suspend fun getLastQuery(): QueryEntity? {
        return queryDao.getLastQuery()
    }

    override suspend fun updateWebsiteLink(id: Int, websiteLink: String) {
        queryDao.updateWebsiteLink(id, websiteLink)
    }
}
