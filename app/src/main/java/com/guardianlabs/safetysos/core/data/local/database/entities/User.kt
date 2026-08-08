package com.guardianlabs.safetysos.core.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(

    @PrimaryKey
    val userId: String,

    val username: String,
    val phone: String,
    val passwordHash: String
) 