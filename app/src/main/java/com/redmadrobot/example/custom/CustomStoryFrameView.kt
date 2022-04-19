package com.redmadrobot.example.custom

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.redmadrobot.example.databinding.ViewCustomStoryFrameBinding
import com.redmadrobot.stories.models.StoryFrame
import com.redmadrobot.stories.stories.views.BaseStoryFrameView
import com.redmadrobot.stories.utils.setImageWithGlide

class CustomStoryFrameView(context: Context) : BaseStoryFrameView(context) {

    private val binding = ViewCustomStoryFrameBinding.inflate(LayoutInflater.from(context)).apply {
        addView(root)
    }

    override fun onFrameSet(frame: StoryFrame) {
        val textColor = Color.parseColor(frame.content.textColor)
        binding.textCustomViewNotice.setTextColor(textColor)
        binding.textTitle.setTextColor(textColor)
        binding.textError.setTextColor(textColor)

        binding.textTitle.text = frame.content.header1

        binding.image.setImageWithGlide(
            imageUrl = frame.imageUrl,
            onReady = { listener?.onLoaded() },
            onFailed = { binding.textError.isVisible = true }
        )
    }
}
