package ua.zloyhr.newsnow.data

import kotlinx.coroutines.flow.MutableStateFlow
import ua.zloyhr.newsnow.data.api.Article
import ua.zloyhr.newsnow.data.api.NewsAPI
import ua.zloyhr.newsnow.data.database.ArticleDao
import javax.inject.Inject

class NewsRepository @Inject constructor(private val api: NewsAPI, private val dao: ArticleDao) {
    suspend fun getBreakingNews(countryCode: String, page: Int) =
        api.getBreakingNews(countryCode, page)

    suspend fun searchNews(searchQuery: String, page: Int) = api.searchNews(searchQuery, page)

    fun getSavedNews() = dao.getAllArticles()

    suspend fun upsert(article: Article) = dao.upsert(article)

    suspend fun delete(article: Article) = dao.delete(article)
}