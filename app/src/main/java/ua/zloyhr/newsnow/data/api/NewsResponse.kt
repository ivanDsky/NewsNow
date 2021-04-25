package ua.zloyhr.newsnow.data.api

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)