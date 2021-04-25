package ua.zloyhr.newsnow.data.database

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ua.zloyhr.newsnow.data.api.Article

@Dao
interface ArticleDao {
    @Insert(onConflict = REPLACE)
    suspend fun upsert(article: Article): Long

    @Delete
    suspend fun delete(article: Article)

    @Query("SELECT * FROM articles_table")
    fun getAllArticles(): Flow<List<Article>>

}