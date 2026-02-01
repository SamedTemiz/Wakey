package com.wakey.app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wakey.app.data.converter.Converters
import com.wakey.app.data.dao.AlarmDao
import com.wakey.app.data.model.Alarm

/**
 * Room Database for Wakey application.
 * Contains the alarms table and provides access to AlarmDao.
 */
@Database(
    entities = [Alarm::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AlarmDatabase : RoomDatabase() {
    
    abstract fun alarmDao(): AlarmDao
    
    companion object {
        const val DATABASE_NAME = "wakey_database"
    }
}
