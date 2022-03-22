package com.redmadrobot.example.api.model

import com.redmadrobot.stories.models.Story

data class StoriesDto(
    val id: String,
    val title: String,
    val image: String,
    val frames: List<FrameDto>
) {
    fun toStory(): Story = Story(
        id = id,
        name = "",
        isSeen = false,
        previewUrl = image,
        title = title,
        frames = frames.map { it.toFrame() }
    )
}
