package com.hyperone.newsapp.di

import android.content.Context
import androidx.room.Room
import com.hyperone.newsapp.localDatabase.LocalDataBase
import com.hyperone.newsapp.localDatabase.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): LocalDataBase {
        return Room.databaseBuilder(
            appContext, LocalDataBase::class.java, "news_database"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    fun provideUserDao(db: LocalDataBase): UserDao {
        return db.userDao()
    }
}