package com.hyperone.newsapp.localDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hyperone.newsapp.authentication.model.UserModel
import com.hyperone.newsapp.favorite.model.FavoriteArticleEntity
import com.hyperone.newsapp.home.model.ArticleEntity
import com.hyperone.newsapp.home.model.BreakingNewsEntity
import com.hyperone.newsapp.localDatabase.dao.FavoriteArticleDao
import com.hyperone.newsapp.localDatabase.dao.NewsDao
import com.hyperone.newsapp.localDatabase.dao.UserDao

@Database(
    entities = [UserModel::class, ArticleEntity::class, BreakingNewsEntity::class, FavoriteArticleEntity::class],
    version = 6,
    exportSchema = false
)
abstract class LocalDataBase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun newsDao(): NewsDao
    abstract fun favoriteArticleDao(): FavoriteArticleDao
}
