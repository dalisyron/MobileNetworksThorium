package com.example.thorium.di

import android.content.Context
import androidx.room.Room
import com.example.data.datasource.DataStoreManager
import com.example.data.datasource.TrackingLocalDataSource
import com.example.data.repository.AppStateRepositoryImpl
import com.example.data.repository.PreferenceRepositoryImpl
import com.example.data.repository.TrackingRepositoryImpl
import com.example.network.DnsMonitoringService
import com.example.network.DnsMonitoringServiceImpl
import com.example.thorium.dao.CellLogDao
import com.example.thorium.dao.TrackingDao
import com.example.thorium.database.MainDatabase
import com.example.thorium.database.MainTypeConverters
import com.example.thorium.datasource.DataStoreManagerImpl
import com.example.thorium.datasource.TrackingLocalDataSourceImpl
import com.example.thorium.service.cellular.CellularService
import com.example.thorium.service.cellular.CellularServiceImpl
import com.example.thorium.service.ping.PingService
import com.example.thorium.service.ping.PingServiceImpl
import com.example.thorium.service.throughput.ThroughputMonitoringService
import com.example.thorium.service.throughput.ThroughputMonitoringServiceImpl
import com.example.usecase.repository.AppStateRepository
import com.example.usecase.repository.PreferenceRepository
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
        ).addTypeConverter(MainTypeConverters()).allowMainThreadQueries().build()
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

    @Singleton
    @Provides
    fun providePreferenceRepository(dataStoreManager: DataStoreManager): PreferenceRepository {
        return PreferenceRepositoryImpl(dataStoreManager)
    }

    @Singleton
    @Provides
    fun provideAppStateRepository(): AppStateRepository {
        return AppStateRepositoryImpl()
    }

    @Singleton
    @Provides
    fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager {
        return DataStoreManagerImpl(context)
    }

    @Singleton
    @Provides
    fun provideCellularService(@ApplicationContext context: Context): CellularService {
        return CellularServiceImpl(context)
    }

    @Singleton
    @Provides
    fun provideDnsMonitoringService(): DnsMonitoringService {
        return DnsMonitoringServiceImpl()
    }

    @Singleton
    @Provides
    fun providePingService(): PingService {
        return PingServiceImpl()
    }


    @Singleton
    @Provides
    fun provideThroughputMonitoringService(@ApplicationContext context: Context): ThroughputMonitoringService {
        return ThroughputMonitoringServiceImpl(context)
    }
}