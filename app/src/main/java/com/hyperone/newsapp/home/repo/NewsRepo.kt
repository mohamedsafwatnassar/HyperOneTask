package com.hyperone.newsapp.home.repo

import com.hyperone.newsapp.base.BaseRepository
import com.hyperone.newsapp.home.model.ArticleEntity
import com.hyperone.newsapp.home.model.ArticlesItem
import com.hyperone.newsapp.home.model.BreakingNewsEntity
import com.hyperone.newsapp.localDatabase.dao.NewsDao
import com.hyperone.newsapp.network.ApiService
import com.hyperone.newsapp.network.DataHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NewsRepo @Inject constructor(
    private val apiService: ApiService,
    private val newsDao: NewsDao
) : BaseRepository() {

    fun getBreakingNews(): Flow<DataHandler<List<BreakingNewsEntity>>> = flow {
        try {
            val response = apiService.getTopHeadlines(category = "Sports")
            if (response.isSuccessful) {
                response.body()?.let { newsResponse ->
                    newsDao.insertBreakingNews(newsResponse.articles!!.map { it.toBreakingNewsEntity() })
                    emit(DataHandler.Success(newsDao.getBreakingNews().first()))
                }
            } else {
                emit(DataHandler.ServerError(response.message()))
            }
        } catch (e: Exception) {
            emit(DataHandler.Error(e.localizedMessage ?: "Unknown Error"))
        }
    }

    fun getAllNews(): Flow<DataHandler<List<ArticleEntity>>> = flow {
        try {
            val response = apiService.getNewsByTopic(query = "technology")
            if (response.isSuccessful) {
                response.body()?.let { newsResponse ->
                    newsDao.insertAllNews(newsResponse.articles!!.map { it.toAllNewsEntity() })
                    emit(DataHandler.Success(newsDao.getAllNews().first()))
                }
            } else {
                emit(DataHandler.ServerError(response.message()))
            }
        } catch (e: Exception) {
            emit(DataHandler.Error(e.localizedMessage ?: "Unknown Error"))
        }
    }

    // Extension functions to map DTO to Room entities.
    private fun ArticlesItem.toBreakingNewsEntity(): BreakingNewsEntity {
        return BreakingNewsEntity(title = title.toString(), urlToImage = urlToImage.toString())
    }

    private fun ArticlesItem.toAllNewsEntity(): ArticleEntity {
        return ArticleEntity(
            title = title,
            description = description.toString(),
            author = author.toString(),
            urlToImage = urlToImage,
            url = url,
            publishedAt = publishedAt.toString()
        )
    }

    // Fetch news articles by search query
    suspend fun searchNews(query: String): DataHandler<List<ArticleEntity>> {
        return try {
            val response = apiService.getNewsByTopic(query = query)
            if (response.isSuccessful) {
                DataHandler.Success(response.body()?.articles!!.map { it.toAllNewsEntity() })
            } else {
                DataHandler.Error("Failed to fetch search results")
            }
        } catch (e: Exception) {
            DataHandler.Error("Network error: ${e.message}")
        }
    }

    // Fetch news articles by category
    suspend fun getNewsByCategory(category: String): DataHandler<List<ArticleEntity>> {
        return try {
            val response = apiService.getTopHeadlines(category = category)
            if (response.isSuccessful) {
                DataHandler.Success(response.body()?.articles!!.map { it.toAllNewsEntity() })
            } else {
                DataHandler.Error("Failed to fetch articles by category")
            }
        } catch (e: Exception) {
            DataHandler.Error("Network error: ${e.message}")
        }
    }
}