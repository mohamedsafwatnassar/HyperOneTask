package com.hyperone.newsapp.localDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hyperone.newsapp.localDatabase.dao.UserDao
import com.hyperone.newsapp.model.UserModel

@Database(entities = [UserModel::class], version = 1, exportSchema = false)
abstract class LocalDataBase : RoomDatabase() {

    abstract fun userDao(): UserDao
}
