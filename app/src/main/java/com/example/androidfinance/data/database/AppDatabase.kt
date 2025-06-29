package com.example.androidfinance.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.androidfinance.data.converter.Converters
import com.example.androidfinance.data.dao.RecordDao
import com.example.androidfinance.data.entity.RecordEntity

@Database(
    entities = [RecordEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
}

object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "finance_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
