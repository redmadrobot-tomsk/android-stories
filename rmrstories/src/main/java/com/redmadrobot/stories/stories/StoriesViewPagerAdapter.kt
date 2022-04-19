package com.redmadrobot.stories.stories

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.redmadrobot.stories.models.Story
import com.redmadrobot.stories.stories.views.BaseStoryFrameView

/**
 * Adapter used by [androidx.viewpager2.widget.ViewPager2] to display one specific story.
 *
 * To use custom [BaseStoryFrameView], pass lambda as [createStoryFragment] parameter,
 * which should create [StoryFragment] derived class in its' body.
 *
 * Due to the fact that the initial element of viewpager2 always has index 0
 * clicking on any non-zero index element viewpager2 considers it to be a scroll
 * which is why onPageSelected is called before createFragment when the fragment is first opened
 * and first story frame doesn't start
 *
 * To avoid this error was added a callback when the first non-zero index fragment was created
 *
 * @see [BaseStoryFrameView], [StoryFragment].
 */
class StoriesViewPagerAdapter(
    activity: FragmentActivity,
    private val stories: ArrayList<Story>,
    private val callback: () -> Unit,
    private val createStoryFragment: ((story: Story) -> StoryFragment)? = null
) : FragmentStateAdapter(activity) {

    private var isFirstLoading = true

    override fun createFragment(position: Int): Fragment {
        val storyFragment: StoryFragment = createStoryFragment?.invoke(stories[position])
            ?: StoryFragment.newStoryFragmentInstance(stories[position])

        return storyFragment.apply {
            if (position != 0 && isFirstLoading) {
                callback.invoke()
            }
            isFirstLoading = false
        }
    }

    override fun getItemCount(): Int = stories.size
}
