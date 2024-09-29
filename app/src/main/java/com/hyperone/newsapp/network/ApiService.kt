package com.hyperone.newsapp.network

import com.hyperone.newsapp.BuildConfig
import com.hyperone.newsapp.home.model.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    // {"status":"error","code":"parametersMissing","message":"Required parameters are missing.
    // Please set any of the following parameters and try again: sources, q, language, country, category."}
    @GET("top-headlines")
    suspend fun getTopHeadlines( // horizontal recycler
        @Query("category") category: String,
        @Query("country") country: String = "us",
        @Query("apiKey") apiKey: String = BuildConfig.api_key
    ): Response<NewsResponse>

    @GET("everything")
    suspend fun getNewsByTopic( // vertical recycler
        @Query("q") query: String, // technology
        @Query("apiKey") apiKey: String = BuildConfig.api_key
    ): Response<NewsResponse>
}