package com.guardianlabs.safetysos.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.guardianlabs.safetysos.core.data.local.database.dao.UserDao
import com.guardianlabs.safetysos.core.data.local.database.entities.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 2
)
abstract class Database : RoomDatabase() {

    abstract fun userDao(): UserDao
}