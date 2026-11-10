package com.resq.core.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.resq.core.data.local.database.entities.PresetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PresetDao {

    // Fetch all presets reactively as a Flow so your UI updates automatically
    @Query("SELECT * FROM presets WHERE savedBy = :savedBy")
    fun getAllPresets(savedBy: String): Flow<List<PresetEntity>>

    // Fetch a single preset by ID if you need to edit or trigger it specifically
    @Query("SELECT * FROM presets WHERE id = :presetId AND savedBy = :savedBy")
    suspend fun getPresetById(presetId: Long, savedBy: String): PresetEntity?

    // Insert a new preset (replaces if it conflicts)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: PresetEntity)

    // Update an existing preset
    @Update
    suspend fun updatePreset(preset: PresetEntity)
    
    @Query("DELETE FROM presets WHERE savedBy = :savedBy AND id = :presetId")
    suspend fun deletePreset(savedBy: String, presetId: Long)

    // Optional: Delete all presets if a user clears data / logs out
    @Query("DELETE FROM presets WHERE savedBy = :savedBy")
    suspend fun deleteAllPresets(savedBy: String)
    
    @Query("UPDATE presets SET isDefault = 0 WHERE savedBy = :savedBy")
    suspend fun clearAllDefaults(savedBy: String)

    @Query("UPDATE presets SET isDefault = 1 WHERE id = :presetId AND savedBy = :savedBy")
    suspend fun setDefault(presetId: Long, savedBy: String)

}
