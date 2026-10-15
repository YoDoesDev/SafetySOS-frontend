package com.resq.core.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.resq.core.data.local.database.entities.UserEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getUser(): UserEntity?

    @Upsert
    suspend fun setUser(user: UserEntity)
    
    @Query("SELECT phone FROM users LIMIT 1")
    suspend fun getPhone(): String

    @Query("DELETE FROM users")
    suspend fun clearUser()
}