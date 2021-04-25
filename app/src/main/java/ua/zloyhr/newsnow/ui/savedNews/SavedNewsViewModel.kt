package ua.zloyhr.newsnow.ui.savedNews

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import ua.zloyhr.newsnow.data.NewsRepository
import ua.zloyhr.newsnow.data.api.Article
import ua.zloyhr.newsnow.data.api.NewsAPI
import ua.zloyhr.newsnow.data.api.NewsResponse
import ua.zloyhr.newsnow.data.database.ArticleDao
import ua.zloyhr.newsnow.util.Resource
import javax.inject.Inject

@HiltViewModel
class SavedNewsViewModel @Inject constructor(private val newsRepository: NewsRepository) : ViewModel() {
    val savedNews = newsRepository.getSavedNews()

    fun delete(article: Article) = viewModelScope.launch {
        newsRepository.delete(article)
    }

    fun upsert(article: Article) = viewModelScope.launch{
        newsRepository.upsert(article)
    }
}