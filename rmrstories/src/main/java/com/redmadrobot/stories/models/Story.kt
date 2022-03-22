package com.redmadrobot.stories.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * A single story entity.
 *
 * @see StoryFrame
 *
 * @property[name] Story name.
 *
 * @property[isSeen] Indicates whether the story was viewed or not.
 *
 * @property[previewUrl] Url of a preview image of the story.
 *
 * @property[title] Story title.
 *
 * @property[frames] List of story's frames.
 * */
@Parcelize
data class Story(
    val id: String,
    val name: String,
    val isSeen: Boolean,
    val previewUrl: String,
    val title: String,
    val frames: List<StoryFrame>
) : Parcelable
