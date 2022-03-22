package com.redmadrobot.stories.stories.views.progress

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.redmadrobot.stories.databinding.ViewPausableProgressBarBinding
import com.redmadrobot.stories.stories.views.progress.PausableProgressBar.Callback
import com.redmadrobot.stories.stories.views.progress.PausableProgressBar.ProgressBarAnimation

/**
 * View for tracking story's progress, similar to Instagram.
 *
 * Can stop and notify when the progress has started/stopped
 * (see [Callback.onStartProgress], [Callback.onFinishProgress]).
 *
 * [ProgressBarAnimation] is used to track progress.
 * Don't forget to clear [ProgressBarAnimation] field in activity's/fragment's lifecycle methods.
 * */
class PausableProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_PROGRESS_DURATION = 7000L
    }

    var duration = DEFAULT_PROGRESS_DURATION
        set(value) {
            field = value
            binding.progressBar.max = value.toInt()
        }

    var callback: Callback? = null
    var isPlaying = false

    private val binding =
        ViewPausableProgressBarBinding.inflate(LayoutInflater.from(context)).apply { addView(root) }

    private var animation: ProgressBarAnimation? = null

    interface Callback {
        fun onStartProgress()
        fun onFinishProgress()
    }

    init {
        binding.progressBar.max = duration.toInt()
    }

    fun setMin() = with(binding) {
        clearAnim()
        progressBar.clearAnimation()
        progressBar.progress = 0
    }

    fun setMax() = with(binding) {
        clearAnim()
        progressBar.clearAnimation()
        progressBar.progress = progressBar.max
    }

    fun changeProgressColor(@ColorRes backgroundColor: Int, @ColorRes progressColor: Int) =
        with(binding) {
            progressBar.progressBackgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(context, backgroundColor)
            )

            progressBar.progressTintList = ColorStateList.valueOf(
                ContextCompat.getColor(context, progressColor)
            )
        }

    fun startProgress() = with(binding) {
        animation = ProgressBarAnimation(
            progressBar = progressBar,
            from = 0f,
            to = progressBar.max.toFloat()
        ).apply {
            duration = this@PausableProgressBar.duration
            interpolator = LinearInterpolator()
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    callback?.onStartProgress()
                }

                override fun onAnimationRepeat(animation: Animation) = Unit

                override fun onAnimationEnd(animation: Animation) {
                    // from 24 to 27 sdk api onAnimationEnd called twice
                    // Clearing animation here helps to fix this issue
                    setAnimationListener(null)
                    isPlaying = false
                    callback?.onFinishProgress()
                }
            })
            fillAfter = true
        }

        isPlaying = true
        progressBar.startAnimation(animation)
    }

    fun pauseProgress() {
        animation?.pause()
        isPlaying = false
    }

    fun resumeProgress() {
        animation?.resume()
        isPlaying = true
    }

    fun clearAnim() {
        animation?.setAnimationListener(null)
        animation?.cancel()
        animation = null
        isPlaying = false
    }

    inner class ProgressBarAnimation(
        private val progressBar: ProgressBar,
        private val from: Float,
        private val to: Float
    ) : Animation() {

        private var isPause = false
        private var elapsedAtPause = -1L

        override fun getTransformation(
            currentTime: Long,
            outTransformation: Transformation?
        ): Boolean {
            if (isPause && elapsedAtPause == 0L) {
                elapsedAtPause = currentTime - startTime
            }
            if (isPause) {
                startTime = currentTime - elapsedAtPause
            }
            return super.getTransformation(currentTime, outTransformation)
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            super.applyTransformation(interpolatedTime, t)
            val value = from + (to - from) * interpolatedTime
            progressBar.progress = value.toInt()
        }

        fun pause() {
            elapsedAtPause = 0
            isPause = true
        }

        fun resume() {
            isPause = false
        }
    }
}
