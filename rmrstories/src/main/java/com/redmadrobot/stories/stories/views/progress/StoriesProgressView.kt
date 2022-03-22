package com.redmadrobot.stories.stories.views.progress

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.redmadrobot.stories.R
import com.redmadrobot.stories.models.StoryFrameControlsColor

/**
 * Adds right amount of [PausableProgressBar] and controls its progress.
 *
 * [destroy] stops [PausableProgressBar]'s animations.
 *
 * Don't forget to call [destroy] in activity's/fragment's lifecycle methods.
 * */
class StoriesProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private companion object {
        val PROGRESS_BAR_LAYOUT_PARAM = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
        val SPACE_LAYOUT_PARAM = LayoutParams(16, LayoutParams.WRAP_CONTENT)
    }

    private val progressBars = mutableListOf<PausableProgressBar>()

    /**
     * Set story count and create views
     */
    var storiesCount = 0
        set(value) {
            field = value
            bindViews()
            progressBars.forEachIndexed { pos, progressBar ->
                progressBar.callback = createProgressCallback(pos)
            }
        }

    var storyProgressListener: StoryProgressListener? = null

    private var current = 0
    private var isComplete: Boolean = false

    interface StoryProgressListener {
        fun onNext()
        fun onPrev()
        fun onComplete()
    }

    init {
        orientation = HORIZONTAL
        bindViews()
    }

    private fun bindViews() {
        progressBars.clear()
        removeAllViews()

        for (i in 0 until storiesCount) {
            val progressBar = createProgressBar()
            progressBars.add(progressBar)
            addView(progressBar)
            if (i + 1 < storiesCount) {
                addView(createSpace())
            }
        }
    }

    private fun createProgressBar(): PausableProgressBar {
        val p = PausableProgressBar(context)
        p.id = View.generateViewId()
        p.layoutParams = PROGRESS_BAR_LAYOUT_PARAM
        return p
    }

    private fun createSpace(): View {
        val v = View(context)
        v.layoutParams = SPACE_LAYOUT_PARAM
        return v
    }

    fun changeProgressColor(controlsColor: StoryFrameControlsColor) {
        val backgroundColor = when (controlsColor) {
            StoryFrameControlsColor.DARK -> R.color.black_60
            else -> R.color.white_20
        }

        val progressColor = when (controlsColor) {
            StoryFrameControlsColor.DARK -> R.color.black
            else -> R.color.white
        }

        progressBars.forEach {
            it.changeProgressColor(backgroundColor, progressColor)
        }
    }

    fun setStoryDuration(duration: Long) {
        progressBars.forEach {
            it.duration = duration
        }
    }

    /**
     * Set start progress position
     *
     * @param from - position progress bar
     */
    fun setStartStory(from: Int = 0) {
        for (i in 0 until from) {
            progressBars[i].setMax()
        }
        for (i in from until progressBars.size) {
            progressBars[i].setMin()
        }
        current = from
    }

    /**
     * Start progress animation
     */
    fun startProgress() {
        if (!progressBars[current].isPlaying) {
            progressBars[current].startProgress()
        }
    }

    /**
     * Set max value for current progress.
     * Increment {current}
     */
    fun next() {
        progressBars[current].setMax()
        if (++current >= progressBars.size) {
            --current
        }
    }

    /**
     * Set min value for current position
     */
    fun reverse() {
        progressBars[current].setMin()
    }

    /**
     * Set min value for current position.
     * Decrement {current}
     */
    fun previous() {
        progressBars[current].setMin()
        if (--current < 0) {
            ++current
        }
    }

    /**
     * Pause current progress animation
     */
    fun pause() {
        if (current < 0) return
        progressBars[current].pauseProgress()
    }

    /**
     * Resume current progress animation
     */
    fun resume() {
        if (current < 0) return
        progressBars[current].resumeProgress()
    }

    /**
     * Clear progress animation.
     * Need to call when Activity or Fragment destroy.
     */
    fun destroy() {
        progressBars.forEach {
            it.clearAnim()
        }
    }

    private fun createProgressCallback(index: Int): PausableProgressBar.Callback {
        return object : PausableProgressBar.Callback {

            override fun onStartProgress() {
                current = index
            }

            override fun onFinishProgress() {
                if (++current <= progressBars.size - 1) {
                    storyProgressListener?.onNext()
                } else {
                    --current
                    isComplete = true
                    storyProgressListener?.onComplete()
                }
            }
        }
    }
}
