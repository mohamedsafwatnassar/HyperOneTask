package com.hyperone.newsapp.home.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyperone.newsapp.home.model.ArticleEntity
import com.hyperone.newsapp.home.model.BreakingNewsEntity
import com.hyperone.newsapp.home.repo.NewsRepo
import com.hyperone.newsapp.network.DataHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repo: NewsRepo,
) : ViewModel() {

    private val _breakingNews = MutableLiveData<DataHandler<List<BreakingNewsEntity>>>()
    val breakingNews: LiveData<DataHandler<List<BreakingNewsEntity>>> = _breakingNews

    private val _allNews = MutableLiveData<DataHandler<List<ArticleEntity>>>()
    val allNews: LiveData<DataHandler<List<ArticleEntity>>> = _allNews

    private val _filteredArticles = MutableLiveData<DataHandler<List<ArticleEntity>>>()
    val filteredArticles: LiveData<DataHandler<List<ArticleEntity>>> = _filteredArticles

    init {
        fetchBreakingNews()
        fetchAllNews()
    }

    private fun fetchBreakingNews() = viewModelScope.launch {
        repo.getBreakingNews().collect {
            _breakingNews.postValue(it)
        }
    }

     fun fetchAllNews() = viewModelScope.launch {
        repo.getAllNews().collect {
            _allNews.postValue(it)
        }
    }

    fun searchArticles(query: String) {
        viewModelScope.launch {
            _filteredArticles.value = DataHandler.ShowLoading
            val result = repo.searchNews(query) // Call repository to search
            _filteredArticles.value = result
        }
    }

    fun filterArticlesByCategory(category: String) {
        viewModelScope.launch {
            _filteredArticles.value = DataHandler.ShowLoading
            val result = repo.getNewsByCategory(category) // Call repository to filter by category
            _filteredArticles.value = result
        }
    }
}

