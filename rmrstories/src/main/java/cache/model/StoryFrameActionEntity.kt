package cache.model

import com.redmadrobot.stories.models.StoryFrameAction

internal data class StoryFrameActionEntity(
    val text: String,
    val url: String
) {
    fun toStoryAction() = StoryFrameAction(
        text = text,
        url = url
    )

    companion object {
        fun fromStoryAction(action: StoryFrameAction?): StoryFrameActionEntity? {
            return if (action == null) {
                null
            } else {
                StoryFrameActionEntity(
                    text = action.text,
                    url = action.url
                )
            }
        }
    }
}
