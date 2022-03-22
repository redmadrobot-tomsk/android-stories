package com.redmadrobot.stories.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Anything that can be displayed on [StoryFrame].
 *
 * @see StoryFrame
 * @see StoryFrameAction
 *
 * @property[controlsColor] Color of story frame's control views (progress and close).
 *
 * @property[showGradients] Specifies where gradient effect should be placed
 * or shouldn't be placed at all.
 *
 * @property[position] Position relative to the [StoryFrame].
 *
 * @property[textColor] Color of story frame's text.
 *
 * @property[header1] Text of the first header. If null, header is invisible.
 *
 * @property[header2] Text of the second header. If null, header is invisible.
 *
 * @property[descriptions] Description of the story frame.
 * Every element of the list will be separated with line break.
 *
 * @property[action] Action that will be performed
 * on action button click (see [StoryFrameAction]).
 * If null, button will be invisible.
 *
 * @property[gradientColor] Color of background's gradient.
 * Can be none if [gradientColor] is null.
 * */
@Parcelize
data class StoryFrameContent(
    val controlsColor: StoryFrameControlsColor,
    val showGradients: StoryFrameShowGradients,
    val position: StoryFrameContentPosition,
    val textColor: String,
    val header1: String?,
    val header2: String?,
    val descriptions: List<String>?,
    val action: StoryFrameAction?,
    val gradientColor: String?
) : Parcelable {

}
