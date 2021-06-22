package com.example.thorium.di

import android.content.Context
import androidx.room.Room
import com.example.data.datasource.TrackingLocalDataSource
import com.example.data.repository.TrackingRepositoryImpl
import com.example.thorium.dao.CellLogDao
import com.example.thorium.dao.TrackingDao
import com.example.thorium.database.MainDatabase
import com.example.thorium.database.MainTypeConverters
import com.example.thorium.datasource.TrackingLocalDataSourceImpl
import com.example.usecase.repository.TrackingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideMainDatabase(@ApplicationContext context: Context): MainDatabase {
        val db = Room.databaseBuilder(
            context,
            MainDatabase::class.java, "main-db"
        ).addTypeConverter(MainTypeConverters()).build()
        return db
    }

    @Singleton
    @Provides
    fun provideTrackingDao(mainDatabase: MainDatabase): TrackingDao {
        return mainDatabase.trackingDao()
    }

    @Singleton
    @Provides
    fun provideCellLogDao(mainDatabase: MainDatabase): CellLogDao {
        return mainDatabase.cellLogDao()
    }

    @Singleton
    @Provides
    fun provideTrackingLocalDataSource(
        trackingDao: TrackingDao,
        cellLogDao: CellLogDao
    ): TrackingLocalDataSource {
        return TrackingLocalDataSourceImpl(trackingDao, cellLogDao)
    }

    @Singleton
    @Provides
    fun provideTrackingRepository(trackingLocalDataSource: TrackingLocalDataSource): TrackingRepository {
        return TrackingRepositoryImpl(trackingLocalDataSource)
    }
}