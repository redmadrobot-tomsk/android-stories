package cache

import android.os.Parcelable
import cache.StoriesConfig.*
import kotlinx.parcelize.Parcelize

/**
 * [All] — Show and delete all stories
 *
 * [Viewed] — Show and delete only viewed stories
 *
 * [NotViewed] — Show and delete only not viewed stories
 *
 * [ByTime] —
 *  #GET        Show stories that are not saved earlier than a specified time
 *  #DELETE     Delete stories that are saved earlier than a specified time
 */
sealed class StoriesConfig : Parcelable {
    @Parcelize
    object All : StoriesConfig()

    @Parcelize
    object Viewed : StoriesConfig()

    @Parcelize
    object NotViewed : StoriesConfig()

    @Parcelize
    data class ByTime(val seconds: Long) : StoriesConfig()

    companion object {
        fun default() = All
    }
}
