package cache.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.redmadrobot.stories.models.Story

@Entity(tableName = "Story")
internal data class StoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val isSeen: Boolean,
    val previewUrl: String,
    val title: String,
    val savedAt: Long = System.currentTimeMillis()
) {
    companion object {
        fun fromStory(story: Story): StoryEntity = StoryEntity(
            id = story.id,
            name = story.name,
            isSeen = story.isSeen,
            previewUrl = story.previewUrl,
            title = story.title
        )
    }
}
