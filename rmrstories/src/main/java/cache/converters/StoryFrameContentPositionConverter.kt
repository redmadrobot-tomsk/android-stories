package cache.converters

import androidx.room.TypeConverter
import com.redmadrobot.stories.models.StoryFrameContentPosition

class StoryFrameContentPositionConverter {

    @TypeConverter
    fun convert(data: StoryFrameContentPosition?): Int {
        return data?.ordinal ?: NULL
    }

    @TypeConverter
    fun convert(data: Int): StoryFrameContentPosition? {
        return StoryFrameContentPosition.values().getOrNull(data)
    }

    private companion object {
        const val NULL = -1
    }
}
