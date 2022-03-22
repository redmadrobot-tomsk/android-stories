package cache.model

import androidx.room.Embedded
import com.redmadrobot.stories.models.StoryFrameContent
import com.redmadrobot.stories.models.StoryFrameContentPosition
import com.redmadrobot.stories.models.StoryFrameControlsColor
import com.redmadrobot.stories.models.StoryFrameShowGradients

internal data class StoryFrameContentEntity(
    val controlsColor: StoryFrameControlsColor,
    val showGradients: StoryFrameShowGradients,
    val position: StoryFrameContentPosition,
    val textColor: String,
    val header1: String?,
    val header2: String?,
    val descriptions: List<String>?,
    val gradientColor: String?,
    @Embedded
    val action: StoryFrameActionEntity?
) {
    fun toStoryFrameContent() = StoryFrameContent(
        controlsColor = controlsColor,
        showGradients = showGradients,
        position = position,
        textColor = textColor,
        header1 = header1,
        header2 = header2,
        descriptions = descriptions,
        action = action?.toStoryAction(),
        gradientColor = gradientColor
    )

    companion object {
        fun fromStoryFrameContent(storyFrameContent: StoryFrameContent): StoryFrameContentEntity =
            StoryFrameContentEntity(
                controlsColor = storyFrameContent.controlsColor,
                showGradients = storyFrameContent.showGradients,
                position = storyFrameContent.position,
                textColor = storyFrameContent.textColor,
                header1 = storyFrameContent.header1,
                header2 = storyFrameContent.header2,
                action = StoryFrameActionEntity.fromStoryAction(storyFrameContent.action),
                descriptions = storyFrameContent.descriptions,
                gradientColor = storyFrameContent.gradientColor
            )
    }
}
