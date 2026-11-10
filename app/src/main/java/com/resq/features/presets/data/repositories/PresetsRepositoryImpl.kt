package com.resq.features.presets.data.repositories

import com.resq.core.data.local.database.dao.PresetDao
import com.resq.core.data.local.database.entities.PresetEntity
import com.resq.features.presets.domain.repositories.PresetsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PresetsRepositoryImpl @Inject constructor(
    private val presetDao: PresetDao
) : PresetsRepository {

    override fun getAllPresets(savedBy: String): Flow<List<PresetEntity>> {
        return presetDao.getAllPresets(savedBy)
    }
    
    override suspend fun savePreset(preset: PresetEntity) {
        return presetDao.insertPreset(preset)
    }
    
    override suspend fun deletePreset(presetId: Long, savedBy: String) {
        return presetDao.deletePreset(savedBy, presetId)
    }
    
    override suspend fun setDefault(presetId: Long, savedBy: String) {
        // Reset all presets for this user to false, then flag the selected preset as default
        presetDao.clearAllDefaults(savedBy)
        presetDao.setDefault(presetId, savedBy)
    }
}
