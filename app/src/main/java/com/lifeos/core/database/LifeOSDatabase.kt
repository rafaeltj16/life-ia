package com.lifeos.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lifeos.core.database.converters.LifeConverters
import com.lifeos.core.database.dao.*
import com.lifeos.core.database.entity.*

@Database(
    entities = [
        TaskEntity::class,
        HabitEntity::class,
        HabitLogEntity::class,
        ProjectEntity::class,
        GoalEntity::class,
        FocusSessionEntity::class,
        FinanceEntity::class,
        HealthEntity::class,
        KnowledgeEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(LifeConverters::class)
abstract class LifeOSDatabase : RoomDatabase() {
    abstract fun taskDao(): LifeTaskDao
    abstract fun habitDao(): LifeHabitDao
    abstract fun habitLogDao(): LifeHabitLogDao
    abstract fun projectDao(): LifeProjectDao
    abstract fun goalDao(): LifeGoalDao
    abstract fun focusDao(): LifeFocusDao
    abstract fun financeDao(): LifeFinanceDao
    abstract fun healthDao(): LifeHealthDao
    abstract fun knowledgeDao(): LifeKnowledgeDao

    companion object {
        @Volatile
        private var INSTANCE: LifeOSDatabase? = null

        fun getInstance(context: Context): LifeOSDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifeOSDatabase::class.java,
                    "lifeos_core_database.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
