package com.redmadrobot.stories.models.exception

internal object StoryInstanceRequired : Throwable(
    "BaseStoryFragment requires Story instance passed in fragment's bundle. " +
            "Make sure that you that you use BaseStoryFragment#addStoryToArguments" +
            "when creating your own BaseStoryFragment implementation."
)