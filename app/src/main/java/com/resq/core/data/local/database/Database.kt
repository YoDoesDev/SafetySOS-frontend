package com.resq.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.resq.core.data.local.database.dao.UserDao
import com.resq.core.data.local.database.entities.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 2
)
abstract class Database : RoomDatabase() {

    abstract fun userDao(): UserDao
}