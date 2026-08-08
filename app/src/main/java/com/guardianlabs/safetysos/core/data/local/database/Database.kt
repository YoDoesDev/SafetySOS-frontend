package com.guardianlabs.safetysos.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.guardianlabs.safetysos.core.data.local.database.dao.UserDao
import com.guardianlabs.safetysos.core.data.local.database.entities.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
}