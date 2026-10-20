package com.resq.core.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.resq.core.data.local.database.entities.ContactsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactsDao {

    @Query("SELECT * FROM emergency_contacts WHERE savedBy = :userPhone ORDER BY id ASC")
    fun getAllContacts(userPhone: String): Flow<List<ContactsEntity>>

    @Query("SELECT * FROM emergency_contacts WHERE id = :id AND savedBy = :userPhone LIMIT 1")
    suspend fun getContactById(id: Long, userPhone: String): ContactsEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM emergency_contacts WHERE savedName = :name AND savedBy = :userPhone LIMIT 1)")
    suspend fun existsByName(name: String, userPhone: String): Boolean
    
    @Query("SELECT EXISTS(SELECT existsOnResQ FROM emergency_contacts WHERE phoneNumber = :phone AND  savedBy = :userPhone LIMIT 1)")
    suspend fun existsOnResQ(phone: String, userPhone: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM emergency_contacts WHERE phoneNumber = :phone AND savedBy = :userPhone LIMIT 1)")
    suspend fun existsByPhone(phone: String, userPhone: String): Boolean

    @Upsert
    suspend fun upsertContact(contact: ContactsEntity)

    @Delete
    suspend fun deleteContact(contact: ContactsEntity)

    @Query("DELETE FROM emergency_contacts WHERE id = :id AND savedBy = :userPhone")
    suspend fun deleteContactById(id: Long, userPhone: String)

    @Query("DELETE FROM emergency_contacts WHERE savedBy = :userPhone")
    suspend fun clearAllContacts(userPhone: String)
}
