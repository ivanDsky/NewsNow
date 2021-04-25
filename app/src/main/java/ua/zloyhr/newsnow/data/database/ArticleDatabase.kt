package ua.zloyhr.newsnow.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ua.zloyhr.newsnow.data.api.Article
import ua.zloyhr.newsnow.util.Converters

@Database(entities = [Article::class],version = 2,exportSchema = false)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase(){
    abstract fun getDao(): ArticleDao
}