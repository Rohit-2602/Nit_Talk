package com.example.nittalk.di

import com.example.nittalk.data.PreferencesManager
import com.example.nittalk.firebase.FirebaseSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseSource(preferencesManager: PreferencesManager) : FirebaseSource = FirebaseSource(preferencesManager)

}