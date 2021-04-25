package ua.zloyhr.newsnow.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ua.zloyhr.newsnow.data.api.NewsAPI
import ua.zloyhr.newsnow.data.database.ArticleDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Provides
    @Singleton
    fun getInterceptor() = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    @Provides
    @Singleton
    fun getOHTTPClient(interceptor: HttpLoggingInterceptor) = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    @Provides
    @Singleton
    fun getRetrofitInstance(httpClient: OkHttpClient):Retrofit = Retrofit.Builder()
        .client(httpClient)
        .baseUrl("https://newsapi.org")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun getAPI(retrofit: Retrofit) = retrofit.create(NewsAPI::class.java)

    @Provides
    @Singleton
    fun getDatabase(app: Application) = Room.databaseBuilder(
        app,
        ArticleDatabase::class.java,
        "article_database.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun getDao(database: ArticleDatabase) = database.getDao()
}