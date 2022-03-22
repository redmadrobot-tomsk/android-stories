package com.redmadrobot.example.api.model

import com.redmadrobot.stories.models.Story

data class StoriesResponse(
    val stories: List<StoriesDto>
) {
    fun toStories(): List<Story> = stories.map { it.toStory() }
}
