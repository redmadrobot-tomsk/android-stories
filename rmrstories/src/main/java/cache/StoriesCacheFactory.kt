package cache

import android.annotation.SuppressLint
import android.app.Application
import cache.StoriesCacheFactory.getInstance
import cache.db.Database
import com.redmadrobot.stories.stories.StoriesController
import kotlinx.coroutines.CoroutineScope

/**
 * Object class used to initialize StoriesController and retrieve it by [getInstance].
 * [StoriesController] is a singleton database that must be initialized on app startup.
 * */
object StoriesCacheFactory {

    /**
     * StoriesCache is a database that should always be present in app's lifecycle,
     * so it's okay to store app's context in the static field.
     * */
    @SuppressLint("StaticFieldLeak")
    private lateinit var storiesCache: StoriesCache

    /**
     * Initialization of stories cache managed by database.
     * You must call this in [Application.onCreate].
     * */
    fun init(app: Application, coroutineScope: CoroutineScope): StoriesCacheFactory {
        storiesCache = StoriesCache(app, coroutineScope, Database.build(app).cacheDao())
        return this
    }

    fun setConfig(config: StoriesConfig): StoriesCacheFactory {
        storiesCache.globalConfig = config
        return this
    }

    fun preloadImages(isPreload: Boolean): StoriesCacheFactory {
        storiesCache.isImagesPreload = isPreload
        return this
    }

    fun getInstance(): StoriesController = if (this::storiesCache.isInitialized) {
        storiesCache
    } else {
        throw Exception("StoriesCache must be initialized. Call StoriesCacheFactory#init(Context, CoroutineScope) before StoriesCacheFactory#getInstance()")
    }
}
