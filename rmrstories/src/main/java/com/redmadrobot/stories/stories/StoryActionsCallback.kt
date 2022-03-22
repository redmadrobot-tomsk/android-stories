package com.redmadrobot.stories.stories

import com.redmadrobot.stories.models.Story

/**
 * Callback interface used to notify [StoriesBaseActivity]
 * of actions done by story [StoryFragment].
 * */
interface StoryActionsCallback {
    fun onStoryActionClicked(url: String)
    fun closeStories()
    fun onCompleteStory()
    fun hasPreviousStory(story: Story): Boolean
}
