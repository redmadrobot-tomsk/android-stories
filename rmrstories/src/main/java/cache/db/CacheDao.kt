package cache.db

import androidx.room.*
import cache.model.StoryEntity
import cache.model.StoryEntityDB
import cache.model.StoryFrameEntity
import com.redmadrobot.stories.models.Story
import kotlinx.coroutines.flow.Flow

@Dao
internal abstract class CacheDao {

    // region insert

    @Transaction
    open fun addData(story: List<Story>) {
        story.forEach {
            insertStory(StoryEntity.fromStory(it))
            insertStoryFrames(
                it.frames.map { frame ->
                    StoryFrameEntity.fromStoryFrame(
                        frame,
                        storyId = it.id
                    )
                }
            )
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun insertStory(storyEntity: StoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun insertStoryFrames(storyFrameEntities: List<StoryFrameEntity>)

    // endregion

    // region get

    @Query("SELECT * FROM Story")
    abstract fun getAllStories(): Flow<List<StoryEntityDB>>

    @Query("SELECT * FROM Story WHERE isSeen = :isViewed")
    abstract fun getViewedStories(isViewed: Boolean): Flow<List<StoryEntityDB>>

    @Transaction
    @Query("SELECT * FROM Story WHERE savedAt > :time")
    abstract fun getStoriesByTime(time: Long): Flow<List<StoryEntityDB>>

    @Transaction
    @Query("SELECT * FROM Story WHERE :id = id")
    abstract fun getStory(id: String): Flow<StoryEntityDB>

    // endregion

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateStory(storyEntities: List<StoryEntity>)

    // region clear

    @Query("DELETE FROM Story")
    abstract fun clearCache()

    @Query("DELETE FROM Story WHERE isSeen = :isViewed")
    abstract fun clearCacheViewed(isViewed: Boolean)

    @Query("DELETE FROM Story Where savedAt < :time")
    abstract fun clearCacheByTime(time: Long)

    // endregion
}
