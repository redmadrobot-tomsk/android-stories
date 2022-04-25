package com.redmadrobot.stories.models

import android.os.Parcelable
import cache.StoriesConfig
import kotlinx.parcelize.Parcelize

/**
 * Input parameters for starting [com.redmadrobot.stories.stories.StoriesBaseActivity].
 *
 * @property[startStoryPosition] Story position to be displayed first. Required parameter.
 * @property[storyConfig] Config for caching. Not required parameter, null by default.
 *
 * @see [com.redmadrobot.stories.stories.StoriesBaseActivity]
 * @see [cache.StoriesConfig]
 * */
@Parcelize
data class StoriesInputParams(
    val startStoryPosition: Int,
    val storyConfig: StoriesConfig? = null
) : Parcelable {
    companion object {
        /**
         * Default parameters, where [startStoryPosition] is 0,
         * [storyConfig] is [StoriesConfig.All].
         * */
        fun createDefaults() = StoriesInputParams(
            startStoryPosition = 0,
            storyConfig = StoriesConfig.default()
        )
    }
}
