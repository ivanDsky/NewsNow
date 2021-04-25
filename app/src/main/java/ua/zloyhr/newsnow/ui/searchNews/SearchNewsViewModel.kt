package ua.zloyhr.newsnow.ui.searchNews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import ua.zloyhr.newsnow.data.NewsRepository
import ua.zloyhr.newsnow.data.api.NewsResponse
import ua.zloyhr.newsnow.util.Resource
import javax.inject.Inject

@HiltViewModel
class SearchNewsViewModel @Inject constructor(private val newsRepository: NewsRepository) : ViewModel() {
    val searchNews: MutableStateFlow<Resource<NewsResponse>> = MutableStateFlow(Resource.Error("Empty search bar"))
    var currentPage = 1
    private var currentResponse:NewsResponse? = null

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNews.value = Resource.Loading()
        val response = newsRepository.searchNews(searchQuery,currentPage)
        searchNews.value = handleSearchNewsResponse(response)
    }


    fun resetSearch(){
        currentPage = 1
        currentResponse = null
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>{
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