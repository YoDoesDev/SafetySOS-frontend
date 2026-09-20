package com.resq.core.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.resq.core.data.local.database.entities.ContactsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactsDao {

    @Query("SELECT * FROM emergency_contacts ORDER BY id ASC")
    fun getAllContacts(): Flow<List<ContactsEntity>>

    @Query("SELECT * FROM emergency_contacts WHERE id = :id LIMIT 1")
    suspend fun getContactById(id: Long): ContactsEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM emergency_contacts WHERE savedName = :name LIMIT 1)")
    suspend fun existsByName(name: String): Boolean
    
    @Query("SELECT EXISTS(SELECT existsOnResQ FROM emergency_contacts WHERE phoneNumber = :phone LIMIT 1)")
    suspend fun existsOnResQ(phone: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM emergency_contacts WHERE phoneNumber = :phone LIMIT 1)")
    suspend fun existsByPhone(phone: String): Boolean

    @Upsert
    suspend fun upsertContact(contact: ContactsEntity)

    @Delete
    suspend fun deleteContact(contact: ContactsEntity)

    @Query("DELETE FROM emergency_contacts WHERE id = :id")
    suspend fun deleteContactById(id: Long)

    @Query("DELETE FROM emergency_contacts")
    suspend fun clearAllContacts()
}
