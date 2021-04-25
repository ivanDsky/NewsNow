package ua.zloyhr.newsnow.util

import androidx.room.TypeConverter
import ua.zloyhr.newsnow.data.api.Source

class Converters {

    @TypeConverter
    fun fromSource(source: Source): String{
        return source.name
    }

    @TypeConverter
    fun toSource(source: String): Source{
        return Source(source,source)
    }

}