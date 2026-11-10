package com.resq.features.presets.domain.usecase

import com.resq.core.data.local.database.entities.PresetEntity
import com.resq.features.presets.domain.repositories.PresetsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPresetsUseCase @Inject constructor(
    private val presetsRepository: PresetsRepository
) {
    operator fun invoke(savedBy: String): Flow<List<PresetEntity>> {
        return presetsRepository.getAllPresets(savedBy)
    }
}
