package com.redmadrobot.example.api.model

import com.redmadrobot.stories.models.StoryFrameAction

data class ActionDto(
    val url: String,
    val name: String
) {
    fun toAction() = StoryFrameAction(url = url, text = name)
}
