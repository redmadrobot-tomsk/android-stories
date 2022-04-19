package com.redmadrobot.stories.stories.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import com.redmadrobot.stories.models.StoryFrame
import com.redmadrobot.stories.stories.StoryFragment
import com.redmadrobot.stories.stories.views.BaseStoryFrameView.StoryFrameListener

/**
 * View that's responsible for handling touch events
 * and calling [StoryFrameListener] methods, depending on the event,
 * thereby giving control to [StoryFragment].
 *
 * Extend this view to make your own story frame.
 * Default implementation is [StoryFrameViewImpl]
 *
 * @see [StoryFragment], [StoryFrameListener], [StoryFrameViewImpl].
 * */
abstract class BaseStoryFrameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private companion object {
        const val TOUCH_TIMEOUT = 300L
    }

    interface StoryFrameListener {
        fun onLoaded()
        fun onNext()
        fun onPrev()
        fun onPause()
        fun onResume()
    }

    /**
     * Is called when new [StoryFrame] is set.
     * You should set data to your views and update here.
     * */
    abstract fun onFrameSet(frame: StoryFrame)

    var storyFrame: StoryFrame? = null
        set(value) {
            field = value ?: return
            onFrameSet(value)
        }

    var listener: StoryFrameListener? = null

    private var lastTouchX = -1F
    private var lastTouchY = -1F

    // Some phones have sensitive sensor. Because of this coordinates will be change after ACTION_DOWN.

    private val rangeSensitiveSensorX
        get() = lastTouchX - 10..lastTouchX + 10

    private val rangeSensitiveSensorY
        get() = lastTouchY - 10..lastTouchY + 10

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return when (event?.action) {
            MotionEvent.ACTION_DOWN -> handleMotionDown(event)
            MotionEvent.ACTION_UP -> handleMotionUp(event)
            else -> super.onTouchEvent(event)
        }
    }

    private fun handleMotionDown(event: MotionEvent): Boolean {
        lastTouchX = event.x
        lastTouchY = event.y
        listener?.onPause()
        return true
    }

    private fun handleMotionUp(event: MotionEvent): Boolean {
        val isSimpleClick = event.eventTime - event.downTime <= TOUCH_TIMEOUT
        if (event.x in rangeSensitiveSensorX && isSimpleClick) {
            if (lastTouchX < width / 4) {
                listener?.onPrev()
            } else {
                listener?.onNext()
            }
        } else {
            listener?.onResume()
        }

        return true
    }
}
