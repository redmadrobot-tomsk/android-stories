package com.redmadrobot.stories.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * Used for story frames that require some action to be preformed on action button click
 * (e.g. process deep link or open url in browser).
 *
 * @see StoryFrameContent
 *
 * @param[text] Text to be displayed on action button.
 *
 * @param[url] Deep link or url that you need to process (e.g. open url in user browser).
 * */
@Parcelize
data class StoryFrameAction(
    val text: String,
    val url: String
) : Parcelable
