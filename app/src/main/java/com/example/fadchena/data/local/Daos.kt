package com.example.fadchena.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanRecordDao {
    @Query("SELECT * FROM scan_records ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<ScanRecordEntity>>

    @Query("SELECT * FROM scan_records ORDER BY timestamp DESC LIMIT 5")
    fun getRecentScans(): Flow<List<ScanRecordEntity>>

    @Query("SELECT * FROM scan_records WHERE id = :id LIMIT 1")
    suspend fun getScanById(id: String): ScanRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanRecordEntity)

    @Query("DELETE FROM scan_records WHERE id = :id")
    suspend fun deleteScanById(id: String)

    @Query("DELETE FROM scan_records")
    suspend fun clearAllScans()
}

@Dao
interface CommunityReportDao {
    @Query("SELECT * FROM community_reports ORDER BY reportedAt DESC")
    fun getAllReports(): Flow<List<CommunityReportEntity>>

    @Query("SELECT * FROM community_reports WHERE status = 'VERIFIED' ORDER BY reportedAt DESC LIMIT 10")
    fun getVerifiedAlerts(): Flow<List<CommunityReportEntity>>

    @Query("SELECT * FROM community_reports WHERE target LIKE '%' || :query || '%'")
    suspend fun findReportsByTarget(query: String): List<CommunityReportEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: CommunityReportEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(reports: List<CommunityReportEntity>)

    @Update
    suspend fun updateReport(report: CommunityReportEntity)

    @Query("DELETE FROM community_reports WHERE id = :id")
    suspend fun deleteReport(id: String)
}
