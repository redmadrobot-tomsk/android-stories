package com.redmadrobot.example.custom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.redmadrobot.example.R
import com.redmadrobot.example.databinding.ViewCustomStoryFrameBinding
import com.redmadrobot.stories.models.StoryFrame
import com.redmadrobot.stories.stories.views.BaseStoryFrameView
import com.redmadrobot.stories.utils.setImageWithGlide

class CustomStoryFrameView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseStoryFrameView(context, attrs, defStyleAttr) {

    private val binding = ViewCustomStoryFrameBinding.inflate(LayoutInflater.from(context)).apply {
        addView(root)
    }

    override fun onFrameSet(frame: StoryFrame) = with(binding) {
        val textColor = try {
            Color.parseColor(frame.content.textColor)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            ContextCompat.getColor(context, R.color.black)
        }
        textCustomViewNotice.setTextColor(textColor)
        textTitle.setTextColor(textColor)
        textError.setTextColor(textColor)

        textTitle.text = frame.content.header1

        image.setImageWithGlide(
            imageUrl = frame.imageUrl,
            onReady = { listener?.onLoaded() },
            onFailed = { textError.isVisible = true }
        )
    }
}
