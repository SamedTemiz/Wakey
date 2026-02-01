package com.wakey.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wakey.app.data.model.Alarm
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Alarm entity.
 * Provides methods to perform database operations on alarms.
 */
@Dao
interface AlarmDao {
    
    /**
     * Get all alarms ordered by time
     */
    @Query("SELECT * FROM alarms ORDER BY hour ASC, minute ASC")
    fun getAllAlarms(): Flow<List<Alarm>>
    
    /**
     * Get all alarms once (for boot receiver)
     */
    @Query("SELECT * FROM alarms ORDER BY hour ASC, minute ASC")
    suspend fun getAllAlarmsOnce(): List<Alarm>
    
    /**
     * Get all enabled alarms
     */
    @Query("SELECT * FROM alarms WHERE isEnabled = 1 ORDER BY hour ASC, minute ASC")
    fun getEnabledAlarms(): Flow<List<Alarm>>
    
    /**
     * Get a specific alarm by ID
     */
    @Query("SELECT * FROM alarms WHERE id = :alarmId")
    suspend fun getAlarmById(alarmId: Int): Alarm?
    
    /**
     * Get a specific alarm by ID as Flow
     */
    @Query("SELECT * FROM alarms WHERE id = :alarmId")
    fun getAlarmByIdFlow(alarmId: Int): Flow<Alarm?>
    
    /**
     * Get the count of all alarms
     */
    @Query("SELECT COUNT(*) FROM alarms")
    suspend fun getAlarmCount(): Int
    
    /**
     * Insert a new alarm
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: Alarm): Long
    
    /**
     * Update an existing alarm
     */
    @Update
    suspend fun updateAlarm(alarm: Alarm)
    
    /**
     * Delete an alarm
     */
    @Delete
    suspend fun deleteAlarm(alarm: Alarm)
    
    /**
     * Delete alarm by ID
     */
    @Query("DELETE FROM alarms WHERE id = :alarmId")
    suspend fun deleteAlarmById(alarmId: Int)
    
    /**
     * Toggle alarm enabled state
     */
    @Query("UPDATE alarms SET isEnabled = :isEnabled WHERE id = :alarmId")
    suspend fun setAlarmEnabled(alarmId: Int, isEnabled: Boolean)
    
    /**
     * Delete all alarms
     */
    @Query("DELETE FROM alarms")
    suspend fun deleteAllAlarms()
}
