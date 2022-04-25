package com.redmadrobot.stories.models

import android.os.Parcelable
import com.redmadrobot.stories.models.StoryIsSeenWhen.*
import kotlinx.parcelize.Parcelize

/**
 * Indicates when story counts as "seen"
 *
 * [IMMEDIATE] - Story "is seen" when firstly open.
 *
 * [ONE] - "Is seen" when the first frame was finished/skipped
 * and switched with the second frame.
 *
 * [TWO] - "Is seen" when the second frame was finished/skipped
 * and switched with the third frame.
 *
 * [LAST_FRAME] - "Is seen" only when all of the frames
 * were finished/skipped and story was finished.
 *
 * @see [StoriesInputParams].
 * */
@Parcelize
enum class StoryIsSeenWhen : Parcelable {
    IMMEDIATE, ONE, TWO, LAST_FRAME;

    companion object {
        fun default() = LAST_FRAME
    }
}
