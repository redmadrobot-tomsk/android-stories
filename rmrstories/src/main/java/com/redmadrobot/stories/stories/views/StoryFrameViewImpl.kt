package com.redmadrobot.stories.stories.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import androidx.annotation.AttrRes
import androidx.core.view.isVisible
import com.redmadrobot.stories.R
import com.redmadrobot.stories.databinding.ViewStoryFrameBinding
import com.redmadrobot.stories.models.StoryFrame
import com.redmadrobot.stories.models.StoryFrameContent
import com.redmadrobot.stories.models.StoryFrameContentPosition
import com.redmadrobot.stories.models.StoryFrameShowGradients
import com.redmadrobot.stories.stories.StoryIntentUtil
import com.redmadrobot.stories.utils.setImageWithGlide

/**
 * Default implementation of [BaseStoryFrameView].
 *
 * @see[BaseStoryFrameView].
 * */
internal class StoryFrameViewImpl @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : BaseStoryFrameView(context, attrs, defStyleAttr) {

    private val binding = ViewStoryFrameBinding.inflate(LayoutInflater.from(context)).apply {
        addView(root)
    }

    override fun onFrameSet(frame: StoryFrame) {
        initImage(frame.imageUrl)
        initContentView(frame.content)
        initBackground(frame.content.showGradients, frame.content.gradientColor)
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

        frameContent.actionClickCallback = { url ->
            StoryIntentUtil.executeStoryAction(context, url)
        }

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
                    this@StoryFrameViewImpl.height / 2,
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
}