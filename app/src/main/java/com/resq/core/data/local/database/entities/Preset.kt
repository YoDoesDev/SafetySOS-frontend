package com.resq.core.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.resq.core.data.local.database.converters.StringListConverter

@Entity(tableName = "presets")
@TypeConverters(StringListConverter::class)
data class PresetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val message: String,
    val isLocationEnabled: Boolean,
    val selectedContacts: List<String> = emptyList(),
    val isDefault: Boolean,
    val savedBy: String
)
