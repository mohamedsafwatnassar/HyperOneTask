package com.hyperone.newsapp.localDatabase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hyperone.newsapp.home.model.ArticleEntity
import com.hyperone.newsapp.home.model.BreakingNewsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreakingNews(news: List<BreakingNewsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNews(news: List<ArticleEntity>)

    @Query("SELECT * FROM breaking_news")
    fun getBreakingNews(): Flow<List<BreakingNewsEntity>>

    @Query("SELECT * FROM articles")
    fun getAllNews(): Flow<List<ArticleEntity>>
}