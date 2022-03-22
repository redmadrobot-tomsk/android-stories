package cache.converters

import androidx.room.TypeConverter

class DescriptionConverter {

    private val separatorValue = "<separator/>"

    @TypeConverter
    fun fromDescription(value: List<String>): String =
        value.joinToString(separator = separatorValue)

    @TypeConverter
    fun toDescription(value: String): List<String> = value.split(separatorValue)
}
