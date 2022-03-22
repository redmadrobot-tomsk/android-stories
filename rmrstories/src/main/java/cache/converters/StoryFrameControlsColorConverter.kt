package cache.converters

import androidx.room.TypeConverter
import com.redmadrobot.stories.models.StoryFrameControlsColor

class StoryFrameControlsColorConverter {

    @TypeConverter
    fun convert(data: StoryFrameControlsColor?): Int {
        return data?.ordinal ?: NULL
    }

    @TypeConverter
    fun convert(data: Int): StoryFrameControlsColor? {
        return StoryFrameControlsColor.values().getOrNull(data)
    }

    private companion object {
        const val NULL = -1
    }
}
