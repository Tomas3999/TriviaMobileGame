package com.fei.semestralnezadanie.backend.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fei.semestralnezadanie.backend.database.converters.Converters
import com.fei.semestralnezadanie.backend.database.dao.TriviaDao
import com.fei.semestralnezadanie.backend.database.entities.QuestionAnswer
import com.fei.semestralnezadanie.backend.database.entities.TriviaResult

@Database(
    entities = [
        TriviaResult::class,
        QuestionAnswer::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract val triviaDao: TriviaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}