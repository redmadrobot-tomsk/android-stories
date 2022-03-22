package cache.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cache.converters.DescriptionConverter
import cache.converters.StoryFrameContentPositionConverter
import cache.converters.StoryFrameControlsColorConverter
import cache.converters.StoryFrameShowGradientsConverter
import cache.model.StoryEntity
import cache.model.StoryFrameEntity

@Database(
    entities = [
        StoryEntity::class,
        StoryFrameEntity::class
    ],
    version = 3
)
@TypeConverters(
    DescriptionConverter::class,
    StoryFrameContentPositionConverter::class,
    StoryFrameControlsColorConverter::class,
    StoryFrameShowGradientsConverter::class
)
internal abstract class CacheDatabase : RoomDatabase() {
    abstract fun cacheDao(): CacheDao
}
