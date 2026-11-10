package com.resq.features.presets.domain.repositories

import com.resq.core.data.local.database.entities.PresetEntity
import kotlinx.coroutines.flow.Flow

interface PresetsRepository {
    fun getAllPresets(savedBy: String): Flow<List<PresetEntity>>
    suspend fun savePreset(preset: PresetEntity)
    suspend fun deletePreset(presetId: Long, savedBy: String)
    suspend fun setDefault(presetId: Long, savedBy: String)
}
