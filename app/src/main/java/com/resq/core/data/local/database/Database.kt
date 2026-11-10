package com.resq.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.resq.core.data.local.database.dao.UserDao
import com.resq.core.data.local.database.entities.UserEntity
import com.resq.core.data.local.database.dao.ContactsDao
import com.resq.core.data.local.database.entities.ContactsEntity
import com.resq.core.data.local.database.dao.PresetDao
import com.resq.core.data.local.database.entities.PresetEntity
import com.resq.core.data.local.database.converters.StringListConverter

@Database(
    entities = [
                   UserEntity::class, 
                   ContactsEntity::class,
                   PresetEntity::class
               ],
    version = 9
)

@TypeConverters(StringListConverter::class)
abstract class Database : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun contactsDao(): ContactsDao
    abstract fun presetDao(): PresetDao
}