package com.wakey.app.data.repository

import com.wakey.app.data.dao.AlarmDao
import com.wakey.app.data.model.Alarm
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing alarm data.
 * Provides a clean API for the UI layer to interact with alarm data.
 */
@Singleton
class AlarmRepository @Inject constructor(
    private val alarmDao: AlarmDao
) {
    companion object {
        const val MAX_ALARMS = 3
    }
    
    /**
     * Get all alarms as Flow
     */
    fun getAllAlarms(): Flow<List<Alarm>> = alarmDao.getAllAlarms()
    
    /**
     * Get all enabled alarms as Flow
     */
    fun getEnabledAlarms(): Flow<List<Alarm>> = alarmDao.getEnabledAlarms()
    
    /**
     * Get a specific alarm by ID
     */
    suspend fun getAlarmById(alarmId: Int): Alarm? = alarmDao.getAlarmById(alarmId)
    
    /**
     * Get a specific alarm by ID as Flow
     */
    fun getAlarmByIdFlow(alarmId: Int): Flow<Alarm?> = alarmDao.getAlarmByIdFlow(alarmId)
    
    /**
     * Get the current alarm count
     */
    suspend fun getAlarmCount(): Int = alarmDao.getAlarmCount()
    
    /**
     * Check if a new alarm can be added (max 3 alarms)
     */
    suspend fun canAddAlarm(): Boolean = getAlarmCount() < MAX_ALARMS
    
    /**
     * Insert a new alarm
     * @return the ID of the inserted alarm, or -1 if max limit reached
     */
    suspend fun insertAlarm(alarm: Alarm): Long {
        if (!canAddAlarm()) return -1
        return alarmDao.insertAlarm(alarm)
    }
    
    /**
     * Update an existing alarm
     */
    suspend fun updateAlarm(alarm: Alarm) = alarmDao.updateAlarm(alarm)
    
    /**
     * Delete an alarm
     */
    suspend fun deleteAlarm(alarm: Alarm) = alarmDao.deleteAlarm(alarm)
    
    /**
     * Delete alarm by ID
     */
    suspend fun deleteAlarmById(alarmId: Int) = alarmDao.deleteAlarmById(alarmId)
    
    /**
     * Toggle alarm enabled state
     */
    suspend fun setAlarmEnabled(alarmId: Int, isEnabled: Boolean) = 
        alarmDao.setAlarmEnabled(alarmId, isEnabled)
    
    /**
     * Delete all alarms
     */
    suspend fun deleteAllAlarms() = alarmDao.deleteAllAlarms()
}
