package com.redmadrobot.stories.stories

/**
 * Listener interface used to control [StoryFragment] story by [StoriesBaseActivity].
 * */
interface StoryListener {
    fun startStory(swipeDirection: SwipeDirection)
    fun resumeStory()
    fun stopStory()
}
