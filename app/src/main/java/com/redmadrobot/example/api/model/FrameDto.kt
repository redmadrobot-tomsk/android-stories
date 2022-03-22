package com.redmadrobot.example.api.model

import com.redmadrobot.stories.models.StoryFrame

data class FrameDto(
    val content: ContentDto,
    val image: String
) {
    fun toFrame() = StoryFrame(content = content.toContent(), imageUrl = image)
}
