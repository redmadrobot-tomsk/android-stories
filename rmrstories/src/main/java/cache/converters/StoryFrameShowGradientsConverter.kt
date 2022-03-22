package cache.converters

import androidx.room.TypeConverter
import com.redmadrobot.stories.models.StoryFrameShowGradients

class StoryFrameShowGradientsConverter {

    @TypeConverter
    fun convert(data: StoryFrameShowGradients?): Int {
        return data?.ordinal ?: NULL
    }

    @TypeConverter
    fun convert(data: Int): StoryFrameShowGradients? {
        return StoryFrameShowGradients.values().getOrNull(data)
    }

    private companion object {
        const val NULL = -1
    }
}
