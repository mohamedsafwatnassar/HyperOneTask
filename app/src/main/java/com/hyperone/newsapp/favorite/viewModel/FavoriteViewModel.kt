package com.hyperone.newsapp.favorite.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyperone.newsapp.favorite.model.FavoriteArticleEntity
import com.hyperone.newsapp.favorite.repo.FavoriteRepo
import com.hyperone.newsapp.home.model.ArticleEntity
import com.hyperone.newsapp.network.DataHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val repo: FavoriteRepo,
) : ViewModel() {

    private val _favoriteArticles = MutableLiveData<DataHandler<List<FavoriteArticleEntity>>>()
    val favoriteArticles: LiveData<DataHandler<List<FavoriteArticleEntity>>> = _favoriteArticles

    init {
        loadFavoriteArticles()
    }

    fun saveArticle(article: ArticleEntity) {
        viewModelScope.launch {
            repo.saveFavoriteArticle(article)
        }
    }

    fun removeArticle(article: FavoriteArticleEntity) {
        viewModelScope.launch {
            repo.removeFavoriteArticle(article)
        }
    }

    fun loadFavoriteArticles() {
        viewModelScope.launch {
            _favoriteArticles.value = repo.getFavoriteArticles()
        }
    }

}

