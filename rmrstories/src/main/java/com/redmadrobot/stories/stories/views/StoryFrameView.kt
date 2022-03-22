package com.redmadrobot.stories.stories.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.core.view.isVisible
import com.redmadrobot.stories.R
import com.redmadrobot.stories.databinding.ViewStoryFrameBinding
import com.redmadrobot.stories.models.StoryFrame
import com.redmadrobot.stories.models.StoryFrameContent
import com.redmadrobot.stories.models.StoryFrameContentPosition
import com.redmadrobot.stories.models.StoryFrameShowGradients
import com.redmadrobot.stories.stories.views.StoryFrameView.StoryFrameListener
import com.redmadrobot.stories.utils.setImageWithGlide

/**
 * View that's responsible for displaying one story frame and positioning of all its elements.
 * This View is also responsible for handling touch events
 * and calling [StoryFrameListener] methods, depending on the event,
 * thereby giving control to [com.redmadrobot.stories.stories.StoryFragment].
 * */
class StoryFrameView @JvmOverloads constructor(
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

    var storyFrame: StoryFrame? = null
        set(value) {
            field = value ?: return
            initImage(value.imageUrl)
            initContentView(value.content)
            initBackground(value.content.showGradients, value.content.gradientColor)
        }

    var listener: StoryFrameListener? = null

    private val binding = ViewStoryFrameBinding.inflate(LayoutInflater.from(context)).apply {
        addView(root)
    }

    private var lastTouchX = -1F
    private var lastTouchY = -1F

    // Some phones have sensitive sensor. Because of this coordinates will be change after ACTION_DOWN.

    private val rangeSensitiveSensorX
        get() = lastTouchX - 10..lastTouchX + 10

    private val rangeSensitiveSensorY
        get() = lastTouchY - 10..lastTouchY + 10

    fun setActionCallback(callback: (String) -> Unit) {
        binding.frameContent.actionClickCallback = callback
    }

    private fun initImage(imageUrl: String) {
        with(binding) {
            progressBar.isVisible = true
            layoutFailed.root.isVisible = false
            frameContent.isVisible = false

            image.setImageWithGlide(
                imageUrl,
                onReady = {
                    progressBar.isVisible = false
                    layoutFailed.root.isVisible = false
                    frameContent.isVisible = true
                    listener?.onLoaded()
                },
                onFailed = {
                    progressBar.isVisible = false
                    layoutFailed.root.isVisible = true

                    if (!layoutFailed.imgRefresh.hasOnClickListeners()) {
                        layoutFailed.imgRefresh.setOnClickListener {
                            initImage(imageUrl)
                        }
                    }
                }
            )
        }
    }

    private fun initContentView(content: StoryFrameContent) = with(binding) {
        frameContent.storyFrameContent = content

        val lp = frameContent.layoutParams as LayoutParams
        if (content.position == StoryFrameContentPosition.BOTTOM) {
            lp.gravity = Gravity.BOTTOM
            frameContent.setPadding(
                0,
                0,
                0,
                resources.getDimensionPixelOffset(R.dimen.story_frame_content_bottom_padding)
            )
        } else {
            lp.gravity = Gravity.TOP
            frameContent.setPadding(
                0,
                resources.getDimensionPixelOffset(R.dimen.story_frame_content_top_padding),
                0,
                0
            )
        }
    }

    private fun initBackground(showGradients: StoryFrameShowGradients, gradientColor: String?) =
        with(binding) {
            viewGradient.apply {
                layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    this@StoryFrameView.height / 2,
                    if (showGradients == StoryFrameShowGradients.TOP) {
                        Gravity.TOP
                    } else {
                        Gravity.BOTTOM
                    }
                )

                setBackgroundResource(getGradient(showGradients))

                if (gradientColor != null) {
                    backgroundTintList = ColorStateList.valueOf(Color.parseColor(gradientColor))
                }
            }
        }

    private fun getGradient(showGradients: StoryFrameShowGradients) =
        when (showGradients) { // StoryFrameShowGradients.BOTH don't process for now
            StoryFrameShowGradients.TOP -> {
                R.drawable.bg_story_content_gradient_270
            }
            StoryFrameShowGradients.BOTTOM -> {
                R.drawable.bg_story_content_gradient_90
            }
            else -> {
                android.R.color.transparent
            }
        }

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
