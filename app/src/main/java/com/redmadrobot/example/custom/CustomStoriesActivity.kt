package com.redmadrobot.example.custom

import android.content.Context
import android.content.Intent
import com.redmadrobot.stories.models.StoriesInputParams
import com.redmadrobot.stories.models.Story
import com.redmadrobot.stories.stories.StoriesBaseActivity
import com.redmadrobot.stories.stories.StoryFragment

class CustomStoriesActivity : StoriesBaseActivity() {
    companion object {
        fun newIntent(
            context: Context,
            storiesInputParams: StoriesInputParams
        ): Intent = newStoriesIntent(
            context = context,
            clazz = CustomStoriesActivity::class.java,
            storiesInputParams = storiesInputParams
        )
    }

    override val createStoryFragment: ((Story) -> StoryFragment) = { story ->
        CustomStoryFragment.newInstance(story)
    }

    override fun onStoryActionClicked(url: String) = Unit
}
