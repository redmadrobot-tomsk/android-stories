package com.redmadrobot.stories.models

import cache.StoriesConfig

/**
* Input parameters for starting [com.redmadrobot.stories.stories.StoriesBaseActivity].
 *
 * @property[startStoryPosition] Story position to be displayed first. Required parameter.
 * @property[storyConfig] Config for caching. Not required parameter, null by default.
 *
 * @see [com.redmadrobot.stories.stories.StoriesBaseActivity]
 * @see [cache.StoriesConfig]
* */
data class StoriesInputParams(
    val startStoryPosition: Int,
    val storyConfig: StoriesConfig? = null
) {
    companion object {
        /**
         * Default parameters, where [startStoryPosition] is 0 and [storyConfig] is null.
         * */
        fun createDefaults() = StoriesInputParams(startStoryPosition = 0, storyConfig = null)
    }
}
