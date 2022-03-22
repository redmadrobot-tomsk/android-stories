package com.redmadrobot.stories.stories

import cache.StoriesConfig
import com.redmadrobot.stories.models.Story
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

/**
 * Controller for working with stories storage
 *
 * Must be initialized [cache.StoriesCacheFactory.init] before using it
 *
 * Uses local or global configuration. Global configuration = [StoriesConfig.All] by default.
 * Global config can be set with [cache.StoriesCacheFactory.setConfig]
 */
interface StoriesController {
    fun getScope(): CoroutineScope
    fun add(stories: List<Story>)
    fun clearAndAdd(config: StoriesConfig? = null, stories: List<Story>)
    fun get(config: StoriesConfig? = null): Flow<List<Story>>
    fun update(stories: List<Story>)
    fun clear(config: StoriesConfig? = null)
}
