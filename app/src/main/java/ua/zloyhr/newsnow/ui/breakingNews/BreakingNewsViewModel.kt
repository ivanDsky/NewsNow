package ua.zloyhr.newsnow.ui.breakingNews

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response
import ua.zloyhr.newsnow.NewsNowApplication
import ua.zloyhr.newsnow.data.NewsRepository
import ua.zloyhr.newsnow.data.api.Article
import ua.zloyhr.newsnow.data.api.NewsAPI
import ua.zloyhr.newsnow.data.api.NewsResponse
import ua.zloyhr.newsnow.data.database.ArticleDao
import ua.zloyhr.newsnow.ui.MainActivity
import ua.zloyhr.newsnow.util.Connection
import ua.zloyhr.newsnow.util.Resource
import javax.inject.Inject

@HiltViewModel
class BreakingNewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    private val connection: Connection
) : ViewModel() {
    val breakingNews: MutableStateFlow<Resource<NewsResponse>> = MutableStateFlow(Resource.Loading())
    var currentPage = 1
    private var currentResponse: NewsResponse? = null

    init {
        getBreakingNews("ua")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        breakingNews.value = Resource.Loading()
        try {
            if (connection.hasNetworkConnection()) {
                val response = newsRepository.getBreakingNews(countryCode, currentPage)
                breakingNews.value = handleBreakingNewsResponse(response)
            } else {
                breakingNews.value = Resource.Error("No internet connection")
            }
        }catch (t: Throwable){
            breakingNews.value = when(t){
                is IOException -> Resource.Error("Network failure")
                else -> Resource.Error("Conversion error")
            }
        }
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let {resultResponse ->
                currentPage++
                if(currentResponse == null){
                    currentResponse = resultResponse
                }else{
                    currentResponse?.articles?.addAll(resultResponse.articles)
                }
                return Resource.Success(currentResponse?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}