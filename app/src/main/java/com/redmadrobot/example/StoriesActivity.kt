package com.redmadrobot.example

import android.content.Context
import android.content.Intent
import com.redmadrobot.stories.models.StoriesInputParams
import com.redmadrobot.stories.stories.StoriesBaseActivity

class StoriesActivity : StoriesBaseActivity() {

    companion object {
        fun newIntent(
            context: Context,
            storiesInputParams: StoriesInputParams
        ): Intent = newStoriesIntent(
            context = context,
            clazz = StoriesActivity::class.java,
            storiesInputParams = storiesInputParams
        )
    }

    override fun onStoryActionClicked(url: String) = Unit
}
