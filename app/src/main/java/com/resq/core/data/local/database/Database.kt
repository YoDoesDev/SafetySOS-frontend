package com.resq.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.resq.core.data.local.database.converters.StringListConverter
import com.resq.core.data.local.database.dao.*
import com.resq.core.data.local.database.entities.*

@Database(
    entities = [
        UserEntity::class,
        ContactsEntity::class,
        PresetEntity::class,
        ChatThreadEntity::class,
        ChatRequestEntity::class,
        ChatMessageEntity::class
    ],
    version = 14
)
@TypeConverters(StringListConverter::class)
abstract class Database : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun contactsDao(): ContactsDao
    abstract fun presetDao(): PresetDao
    abstract fun chatThreadDao(): ChatThreadDao
    abstract fun chatRequestDao(): ChatRequestDao
    abstract fun chatMessageDao(): ChatMessageDao
}