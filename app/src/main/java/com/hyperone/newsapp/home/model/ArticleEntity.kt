package com.hyperone.newsapp.home.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleEntity(

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