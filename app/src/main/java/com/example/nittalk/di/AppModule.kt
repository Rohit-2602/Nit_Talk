package com.example.nittalk.di

import android.content.Context
import androidx.room.Room
import com.example.nittalk.data.PreferencesManager
import com.example.nittalk.db.UserDao
import com.example.nittalk.db.UserDatabase
import com.example.nittalk.firebase.FirebaseSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseSource(preferencesManager: PreferencesManager, userDao: UserDao) : FirebaseSource = FirebaseSource(preferencesManager, userDao)

    @Provides
    @Singleton
    fun provideUserDb(@ApplicationContext context: Context) : UserDatabase =
        Room.databaseBuilder(context, UserDatabase::class.java, "user_database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideUserDao(db: UserDatabase): UserDao =
        db.getUserDao()

}