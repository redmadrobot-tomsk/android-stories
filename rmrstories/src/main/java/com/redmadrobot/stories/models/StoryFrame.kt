package com.redmadrobot.stories.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * A single story frame of the [Story] that has one [imageUrl] and some [content].
 *
 * @see Story, StoryFrameContent
 *
 * @property[imageUrl] Url of an image that will be displayed in the story frame.
 *
 * @property[content] Content of this story frame.
 * */
@Parcelize
data class StoryFrame(
    val imageUrl: String,
    val content: StoryFrameContent
) : Parcelable
