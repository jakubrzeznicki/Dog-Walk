package com.kuba.dogwalk.di

import android.content.Context
import androidx.room.Room
import com.kuba.dogwalk.data.local.DogWalkDatabase
import com.kuba.dogwalk.data.local.dog.DogDao
import com.kuba.dogwalk.data.local.myWalk.MyWalkDao
import com.kuba.dogwalk.data.local.notification.NotificationDao
import com.kuba.dogwalk.other.Constants.DATABASE_NAME
import com.kuba.dogwalk.repositories.DefaultDogWalkRepository
import com.kuba.dogwalk.repositories.DogWalkRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, DogWalkDatabase::class.java, DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideDefaultDogWalkRepository(
        myWalkDao: MyWalkDao,
        notificationDao: NotificationDao,
        dogDao: DogDao
    ) = DefaultDogWalkRepository(myWalkDao, notificationDao, dogDao) as DogWalkRepository


    @Singleton
    @Provides
    fun provideMyWalkDao(
        database: DogWalkDatabase
    ) = database.myWalkDao()

    @Singleton
    @Provides
    fun provideNotificationDao(
        database: DogWalkDatabase
    ) = database.notificationDao()

    @Singleton
    @Provides
    fun provideDogDao(
        database: DogWalkDatabase
    ) = database.dogDao()
}