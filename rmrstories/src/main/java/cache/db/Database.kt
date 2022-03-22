package cache.db

import android.content.Context
import androidx.room.Room

internal object Database {
    fun build(context: Context): CacheDatabase = Room.databaseBuilder(
        context,
        CacheDatabase::class.java,
        "stories_database"
    ).fallbackToDestructiveMigration().build()
}
