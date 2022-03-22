package cache.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.redmadrobot.stories.models.StoryFrame

@Entity(
    tableName = "StoryFrame",
    foreignKeys = [
        ForeignKey(
            onDelete = CASCADE,
            entity = StoryEntity::class,
            childColumns = ["storyId"],
            parentColumns = ["id"]
        )
    ]
)
internal data class StoryFrameEntity(
    @PrimaryKey(autoGenerate = true)
    val storyFrameId: Long = 0,
    val storyId: String,
    val imageUrl: String,
    @Embedded(prefix = "storyContent")
    val storyFrameContent: StoryFrameContentEntity
) {
    fun toStoryFrame() = StoryFrame(
        imageUrl = imageUrl,
        content = storyFrameContent.toStoryFrameContent()
    )

    companion object {
        fun fromStoryFrame(storyFrame: StoryFrame, storyId: String): StoryFrameEntity =
            StoryFrameEntity(
                storyId = storyId,
                imageUrl = storyFrame.imageUrl,
                storyFrameContent = StoryFrameContentEntity.fromStoryFrameContent(storyFrame.content)
            )
    }
}
