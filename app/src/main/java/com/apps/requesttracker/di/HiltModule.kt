package com.apps.requesttracker.di

import android.content.Context
import androidx.room.Room
import com.apps.requesttracker.data.repository.QueryRepositoryImpl
import com.apps.requesttracker.data.db.QueryDao
import com.apps.requesttracker.data.db.QueryDatabase
import com.apps.requesttracker.domain.repository.QueryRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HiltModule {

    @Binds
    abstract fun bindQueryRepository(
        queryRepositoryImpl: QueryRepositoryImpl
    ): QueryRepository

    companion object {
        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext appContext: Context): QueryDatabase {
            return Room.databaseBuilder(
                appContext,
                QueryDatabase::class.java,
                "query_database"
            ).build()
        }

        @Provides
        fun provideQueryDao(db: QueryDatabase): QueryDao {
            return db.queryDao()
        }
    }
}
