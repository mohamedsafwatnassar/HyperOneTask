package com.hyperone.newsapp.favorite.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_articles")
data class FavoriteArticleEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String?,
    val description: String,
    val author: String,
    val urlToImage: String?,
    val url: String?,
    val publishedAt: String,

    var isFavorite: Boolean = false
)