package com.resq.features.presets.domain.usecase

import com.resq.features.presets.domain.repositories.PresetsRepository
import javax.inject.Inject

class SetDefaultUseCase @Inject constructor(
    private val presetsRepository: PresetsRepository
) {
    suspend operator fun invoke(presetId: Long, savedBy: String) {
        presetsRepository.setDefault(presetId, savedBy)
    }
}
