package com.wakey.app.data.converter

import androidx.room.TypeConverter
import com.wakey.app.data.model.TaskType

/**
 * Type converters for Room database.
 * Converts complex types to/from types that Room can persist.
 */
class Converters {
    
    /**
     * Convert TaskType enum to String for storage
     */
    @TypeConverter
    fun fromTaskType(taskType: TaskType): String {
        return taskType.name
    }
    
    /**
     * Convert String to TaskType enum
     */
    @TypeConverter
    fun toTaskType(value: String): TaskType {
        return TaskType.valueOf(value)
    }
}
