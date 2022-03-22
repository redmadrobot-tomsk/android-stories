package com.redmadrobot.stories.models

internal object StoriesStartPositionRequired : Throwable(
    "Required parameter for StoriesBaseActivity StoriesInputParams#startStoryPosition is missing." +
            "You must pass StoriesInputParams by calling StoriesBaseActivity.newStoryIntent" +
            "and start activity with this intent."
)
