package com.redmadrobot.stories.utils

import android.app.Activity
import android.content.Context
import android.transition.Transition
import android.transition.TransitionInflater
import androidx.annotation.TransitionRes

object AnimationUtils {

    fun setEnterTransition(activity: Activity, @TransitionRes transition: Int) {
        activity.window.sharedElementEnterTransition = createTransition(activity, transition)
    }

    fun setExitTransition(activity: Activity, @TransitionRes transition: Int) {
        activity.window.sharedElementExitTransition = createTransition(activity, transition)
    }

    fun createTransition(context: Context, @TransitionRes transition: Int): Transition {
        return TransitionInflater.from(context).inflateTransition(
            transition
        )
    }
}
