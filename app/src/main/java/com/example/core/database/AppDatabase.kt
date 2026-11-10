package com.example.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.features.dashboard.data.local.DashboardDao
import com.example.features.dashboard.data.local.DashboardMetricsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Main Room Database for LifeOS application.
 * Initializes the Room database instance with a thread-safe singleton pattern
 * and provides access to [TaskDao] and associated entity DAOs.
 */
@Database(
    entities = [
        TaskEntity::class,
        SubTaskEntity::class,
        ProjectEntity::class,
        DashboardMetricsEntity::class,
        SyncQueueEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Provides access to [TaskDao] for all task operations (insertion, deletion, updates, queries).
     */
    abstract fun taskDao(): TaskDao

    /**
     * Provides access to [ProjectDao].
     */
    abstract fun projectDao(): ProjectDao

    /**
     * Provides access to [DashboardDao].
     */
    abstract fun dashboardDao(): DashboardDao

    /**
     * Provides access to [SyncQueueDao].
     */
    abstract fun syncQueueDao(): SyncQueueDao

    companion object {
        const val DATABASE_NAME = "lifeos_database"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Returns the singleton instance of [AppDatabase], initializing it if necessary
         * using double-checked locking for thread safety.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Alias for [getDatabase] for standard architectural naming conventions.
         */
        fun getInstance(context: Context): AppDatabase = getDatabase(context)

        /**
         * Callback to pre-populate starter tasks when the database is first created.
         */
        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        seedStarterTasks(database.taskDao())
                    }
                }
            }

            private suspend fun seedStarterTasks(taskDao: TaskDao) {
                try {
                    val starterTasks = listOf(
                        TaskEntity(
                            id = UUID.randomUUID().toString(),
                            title = "Planejamento Semanal",
                            description = "Definir os 3 objetivos prioritários da semana e blocos de foco",
                            priority = "HIGH",
                            dueDate = "Hoje",
                            category = "Produtividade",
                            isCompleted = false,
                            status = "TO_DO",
                            isFavorite = true
                        ),
                        TaskEntity(
                            id = UUID.randomUUID().toString(),
                            title = "Revisar Entregas do Projeto",
                            description = "Checar marcos de progresso e sincronizar com a equipe",
                            priority = "MEDIUM",
                            dueDate = "Amanhã",
                            category = "Trabalho",
                            isCompleted = false,
                            status = "TO_DO"
                        ),
                        TaskEntity(
                            id = UUID.randomUUID().toString(),
                            title = "Organizar Espaço de Foco",
                            description = "Eliminar distrações visuais e preparar ambiente para deep work",
                            priority = "LOW",
                            dueDate = "Esta semana",
                            category = "Geral",
                            isCompleted = false,
                            status = "TO_DO"
                        )
                    )
                    taskDao.insertTasks(starterTasks)
                } catch (e: Exception) {
                    // Fail silently during initial DB seed
                }
            }
        }
    }
}
