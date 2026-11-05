package com.fei.semestralnezadanie.di

import android.content.Context
import com.fei.semestralnezadanie.backend.api.ApiService
import com.fei.semestralnezadanie.backend.api.TriviaApi
import com.fei.semestralnezadanie.backend.database.AppDatabase
import com.fei.semestralnezadanie.backend.database.dao.TriviaDao
import com.fei.semestralnezadanie.backend.datastore.UserManager
import com.fei.semestralnezadanie.backend.repository.TriviaRepository
import com.fei.semestralnezadanie.backend.repository.TriviaRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTriviaApi(): TriviaApi {
        return ApiService.retrofitService
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideTriviaDao(database: AppDatabase): TriviaDao {
        return database.triviaDao
    }

    @Provides
    @Singleton
    fun provideUserManager(@ApplicationContext context: Context): UserManager {
        return UserManager(context)
    }

    @Provides
    @Singleton
    fun provideTriviaRepository(
        apiService: TriviaApi,
        triviaDao: TriviaDao
    ): TriviaRepository {
        return TriviaRepositoryImpl(apiService, triviaDao)
    }
}
