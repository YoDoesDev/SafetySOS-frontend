package com.resq.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.resq.core.data.local.database.dao.UserDao
import com.resq.core.data.local.database.entities.UserEntity
import com.resq.core.data.local.database.dao.ContactsDao
import com.resq.core.data.local.database.entities.ContactsEntity

@Database(
    entities = [
                   UserEntity::class, 
                   ContactsEntity::class
               ],
    version = 4
)
abstract class Database : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun contactsDao(): ContactsDao
}