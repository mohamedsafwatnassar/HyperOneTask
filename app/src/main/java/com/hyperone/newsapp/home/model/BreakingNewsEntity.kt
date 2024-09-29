package com.hyperone.newsapp.home.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "breaking_news")
data class BreakingNewsEntity(
    @PrimaryKey val title: String,

    val urlToImage: String,
)