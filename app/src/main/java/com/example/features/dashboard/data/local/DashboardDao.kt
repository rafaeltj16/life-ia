package com.example.features.dashboard.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DashboardDao {
    @Query("SELECT * FROM dashboard_metrics WHERE userId = :userId LIMIT 1")
    suspend fun getMetrics(userId: String): DashboardMetricsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMetrics(metrics: DashboardMetricsEntity)

    @Query("DELETE FROM dashboard_metrics WHERE userId = :userId")
    suspend fun deleteMetrics(userId: String)
}
