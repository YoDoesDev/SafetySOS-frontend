package com.resq.core.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_contacts")
data class ContactsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val savedName: String,
    
    val phoneNumber: String,
    
    val existsOnResQ: Boolean, 
    
    // Optional username for app-to-app features if the contact uses ResQ
    val username: String? = null
) 

