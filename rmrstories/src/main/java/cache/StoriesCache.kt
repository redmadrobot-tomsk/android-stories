package cache

import android.app.Application
import cache.db.CacheDao
import cache.model.StoryEntity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.redmadrobot.stories.models.Story
import com.redmadrobot.stories.stories.StoriesController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class StoriesCache(
    private val app: Application,
    private val coroutine: CoroutineScope,
    private val cacheDao: CacheDao
) : StoriesController {

    companion object {
        private const val MILLISECONDS = 1000L
    }

    var globalConfig: StoriesConfig = StoriesConfig.All
    var isImagesPreload = false

    override fun getScope(): CoroutineScope = coroutine

    override fun add(stories: List<Story>) {
        coroutine.launch {
            addEager(stories)
        }
    }

    override fun clear(config: StoriesConfig?) {
        coroutine.launch {
            clearEager(config)
        }
    }

    override fun clearAndAdd(config: StoriesConfig?, stories: List<Story>) {
        coroutine.launch {
            clearEager(config)
            addEager(stories)
        }
    }

    override fun get(config: StoriesConfig?): Flow<List<Story>> =
        when (val currentConfig = config ?: globalConfig) {
            is StoriesConfig.All -> cacheDao.getAllStories()
            is StoriesConfig.NotViewed -> cacheDao.getViewedStories(false)
            is StoriesConfig.Viewed -> cacheDao.getViewedStories(true)
            is StoriesConfig.ByTime -> cacheDao.getStoriesByTime(System.currentTimeMillis() - currentConfig.seconds * MILLISECONDS)
        }.map { stories -> stories.map { it.toStory() } }

    override fun update(stories: List<Story>) {
        coroutine.launch {
            cacheDao.updateStory(stories.map { StoryEntity.fromStory(it) })
        }
    }

    private fun addEager(stories: List<Story>) {
        cacheDao.addData(stories)
        if (isImagesPreload) preloadImages(stories)
    }

    private fun clearEager(config: StoriesConfig?) {
        Glide.get(app).clearDiskCache()

        when (val currentConfig = config ?: globalConfig) {
            is StoriesConfig.All -> cacheDao.clearCache()
            is StoriesConfig.Viewed -> cacheDao.clearCacheViewed(true)
            is StoriesConfig.NotViewed -> cacheDao.clearCacheViewed(false)
            is StoriesConfig.ByTime -> cacheDao.clearCacheByTime(System.currentTimeMillis() - currentConfig.seconds * MILLISECONDS)
        }
    }

    private fun preloadImages(stories: List<Story>) {
        coroutine.launch {
            getStoriesImages(stories).forEach { image ->
                Glide.with(app)
                    .load(image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .preload()
            }
        }
    }

    private fun getStoriesImages(stories: List<Story>): List<String> =
        stories.map { story -> story.frames.map { frame -> frame.imageUrl } }.distinct().flatten()
}
