package com.guardianlabs.safetysos.core.database.di

import android.content.Context
import androidx.room.Room
import com.guardianlabs.safetysos.core.data.local.database.Database
import com.guardianlabs.safetysos.core.data.local.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): Database {

        return Room.databaseBuilder(
            context,
            Database::class.java,
            "safetysos.db"
        ).build()
    }

    @Provides
    fun provideUserDao(
        db: Database
    ): UserDao = db.userDao()
}