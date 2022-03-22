package cache.model

import androidx.room.Embedded
import androidx.room.Relation
import com.redmadrobot.stories.models.Story

internal data class StoryEntityDB(
    @Embedded
    val story: StoryEntity,
    @Relation(entity = StoryFrameEntity::class, parentColumn = "id", entityColumn = "storyId")
    val frames: List<StoryFrameEntity>
) {
    fun toStory() = Story(
        id = story.id,
        name = story.name,
        isSeen = story.isSeen,
        previewUrl = story.previewUrl,
        title = story.title,
        frames = frames.map { it.toStoryFrame() }
    )
}
