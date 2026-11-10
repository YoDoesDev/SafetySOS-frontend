package com.resq.features.presets.domain.usecase

import com.resq.core.data.local.database.entities.PresetEntity
import com.resq.features.presets.domain.repositories.PresetsRepository
import javax.inject.Inject

class SavePresetUseCase @Inject constructor(
    private val presetsRepository: PresetsRepository
) {
    suspend operator fun invoke(preset: PresetEntity) {
        presetsRepository.savePreset(preset)
    }
}
