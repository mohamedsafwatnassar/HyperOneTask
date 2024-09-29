package com.hyperone.newsapp.favorite.repo

import com.hyperone.newsapp.base.BaseRepository
import com.hyperone.newsapp.favorite.model.FavoriteArticleEntity
import com.hyperone.newsapp.home.model.ArticleEntity
import com.hyperone.newsapp.localDatabase.dao.FavoriteArticleDao
import com.hyperone.newsapp.network.DataHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavoriteRepo @Inject constructor(
    private val favoriteArticleDao: FavoriteArticleDao
) : BaseRepository() {

    // Save favorite article to Room
    suspend fun saveFavoriteArticle(article: ArticleEntity): DataHandler<FavoriteArticleEntity> {
        val item = FavoriteArticleEntity(
            title = article.title,
            description = article.description,
            urlToImage = article.urlToImage,
            author = article.author,
            url = article.url,
            publishedAt = article.publishedAt
        )
        return withContext(Dispatchers.IO) {
            try {
                favoriteArticleDao.insertFavoriteArticle(item)
                DataHandler.Success(item)
            } catch (e: Exception) {
                DataHandler.Error(e.localizedMessage ?: "Error saving article")
            }
        }
    }

    // Remove favorite article from Room
    suspend fun removeFavoriteArticle(article: FavoriteArticleEntity): DataHandler<FavoriteArticleEntity> {
        val item = FavoriteArticleEntity(
            title = article.title,
            description = article.description,
            urlToImage = article.urlToImage,
            author = article.author,
            url = article.url,
            publishedAt = article.publishedAt
        )
        return withContext(Dispatchers.IO) {
            try {
                favoriteArticleDao.removeFavoriteArticle(item)
                DataHandler.Success(item)
            } catch (e: Exception) {
                DataHandler.Error(e.localizedMessage ?: "Error removing article")
            }
        }
    }

    // Get all favorite articles
    suspend fun getFavoriteArticles(): DataHandler<List<FavoriteArticleEntity>> {
        return withContext(Dispatchers.IO) {
            try {
                val articles = favoriteArticleDao.getAllFavoriteArticles()
                DataHandler.Success(articles)
            } catch (e: Exception) {
                DataHandler.Error(e.localizedMessage ?: "Error fetching favorite articles")
            }
        }
    }
}