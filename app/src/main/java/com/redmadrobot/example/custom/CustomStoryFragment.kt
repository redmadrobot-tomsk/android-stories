package com.redmadrobot.example.custom

import android.content.Context
import com.redmadrobot.stories.models.Story
import com.redmadrobot.stories.models.StoryIsSeenWhen
import com.redmadrobot.stories.stories.StoryFragment
import com.redmadrobot.stories.stories.views.BaseStoryFrameView

class CustomStoryFragment : StoryFragment() {

    companion object {
        fun newInstance(story: Story): StoryFragment =
            CustomStoryFragment().addStoryToArguments(story, StoryIsSeenWhen.TWO)
    }

    override fun createStoryFrameView(context: Context): BaseStoryFrameView =
        CustomStoryFrameView(context)
}
