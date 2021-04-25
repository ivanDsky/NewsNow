package ua.zloyhr.newsnow.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {
    @GET("/v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode:String = "ua",
        @Query("page")
        page:Int = 1,
        @Query("apiKey")
        apiKey:String = "4f177701130c4c7185d2755b6265abae"
    ):Response<NewsResponse>

    @GET("/v2/everything")
    suspend fun searchNews(
        @Query("q")
        searchQuery:String,
        @Query("page")
        page:Int = 1,
        @Query("apiKey")
        apiKey:String = "4f177701130c4c7185d2755b6265abae"
    ):Response<NewsResponse>
}