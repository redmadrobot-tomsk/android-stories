package com.redmadrobot.stories.utils

import android.view.MotionEvent

internal fun MotionEvent.stringAction() = when (action) {
    MotionEvent.ACTION_BUTTON_PRESS -> "ACTION_BUTTON_PRESS"
    MotionEvent.ACTION_BUTTON_RELEASE -> "ACTION_BUTTON_RELEASE"
    MotionEvent.ACTION_CANCEL -> "ACTION_CANCEL"
    MotionEvent.ACTION_DOWN -> "ACTION_DOWN"
    MotionEvent.ACTION_UP -> "ACTION_UP"
    MotionEvent.ACTION_HOVER_ENTER -> "ACTION_HOVER_ENTER"
    MotionEvent.ACTION_HOVER_MOVE -> "ACTION_HOVER_MOVE"
    MotionEvent.ACTION_HOVER_EXIT -> "ACTION_HOVER_EXIT"
    MotionEvent.ACTION_SCROLL -> "ACTION_SCROLL"
    MotionEvent.ACTION_MASK -> "ACTION_MASK"
    MotionEvent.ACTION_MOVE -> "ACTION_MOVE"
    MotionEvent.ACTION_OUTSIDE -> "ACTION_OUTSIDE"
    else -> "ACTION_OTHER"
}
