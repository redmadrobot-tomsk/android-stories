package com.redmadrobot.example.api.model

import com.google.gson.annotations.SerializedName
import com.redmadrobot.stories.models.StoryFrameContent
import com.redmadrobot.stories.models.StoryFrameContentPosition
import com.redmadrobot.stories.models.StoryFrameControlsColor
import com.redmadrobot.stories.models.StoryFrameShowGradients

data class ContentDto(
    @SerializedName("text_color")
    val textColor: String,
    val header1: String,
    val descriptions: List<String>,
    val position: String,
    @SerializedName("controls_color")
    val controlsColor: String,
    val gradient: String,
    @SerializedName("gradient_color")
    val gradientColor: String,
    val action: ActionDto? = null
) {
    fun toContent() = StoryFrameContent(
        controlsColor = StoryFrameControlsColor.valueOf(controlsColor.uppercase()),
        showGradients = StoryFrameShowGradients.valueOf(gradient.uppercase()),
        position = StoryFrameContentPosition.valueOf(position.uppercase()),
        textColor = textColor,
        header1 = header1,
        header2 = null,
        descriptions = descriptions,
        action = action?.toAction(),
        gradientColor = gradientColor
    )
}
