package com.hyperone.newsapp.localDatabase.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hyperone.newsapp.favorite.model.FavoriteArticleEntity

@Dao
interface FavoriteArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteArticle(article: FavoriteArticleEntity)

    @Delete
    suspend fun removeFavoriteArticle(article: FavoriteArticleEntity)

    @Query("SELECT * FROM favorite_articles")
    suspend fun getAllFavoriteArticles(): List<FavoriteArticleEntity>
}