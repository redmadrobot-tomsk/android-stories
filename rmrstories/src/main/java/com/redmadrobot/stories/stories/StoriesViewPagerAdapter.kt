package com.redmadrobot.stories.stories

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.redmadrobot.stories.models.Story

/**
 * Adapter used by [androidx.viewpager2.widget.ViewPager2] to display one specific story.
 *
 * Due to the fact that the initial element of viewpager2 always has index 0
 * clicking on any non-zero index element viewpager2 considers it to be a scroll
 * which is why onPageSelected is called before createFragment when the fragment is first opened
 * and first story frame doesn't start
 *
 * To avoid this error was added a callback when the first non-zero index fragment was created
 */
class StoriesViewPagerAdapter(
    activity: FragmentActivity,
    private val stories: ArrayList<Story>,
    private val callback: () -> Unit
) : FragmentStateAdapter(activity) {

    private var isFirstLoading = true

    override fun createFragment(position: Int): Fragment =
        StoryFragment.newInstance(stories[position]).apply {
            if (position != 0 && isFirstLoading) {
                callback.invoke()
            }
            isFirstLoading = false
        }

    override fun getItemCount(): Int = stories.size
}
