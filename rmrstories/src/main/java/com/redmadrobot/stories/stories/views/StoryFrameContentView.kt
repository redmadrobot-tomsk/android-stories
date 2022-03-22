package com.redmadrobot.stories.stories.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.redmadrobot.stories.R
import com.redmadrobot.stories.databinding.ViewStoryFrameContentBinding
import com.redmadrobot.stories.models.StoryFrameContent

/**
 * Displays the content of [com.redmadrobot.stories.models.StoryFrame].
 * */
class StoryFrameContentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var storyFrameContent: StoryFrameContent? = null
        set(value) {
            field = value ?: return
            initContent(value)
        }

    private var binding = ViewStoryFrameContentBinding.inflate(LayoutInflater.from(context)).apply {
        addView(root)
    }

    private val actionUrl: String?
        get() = storyFrameContent?.action?.url

    var actionClickCallback: ((String) -> Unit)? = null

    private var textContentColor: Int = ContextCompat.getColor(context, R.color.white)

    private fun initContent(content: StoryFrameContent) = with(binding) {
        initContentColor(content.textColor)
        initText(textTitle, content.header1)
        initText(textSubtitle, content.header2)
        initText(textDescription, content.descriptions?.joinToString("\n"))
        initActionBtn(content.action?.text)
    }

    private fun initContentColor(color: String?) {
        color ?: return
        try {
            textContentColor = Color.parseColor(color)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    private fun initText(textView: TextView, text: String?) {
        if (text == null) {
            textView.isVisible = false
            return
        }

        textView.apply {
            isVisible = true
            this.text = text
            setTextColor(textContentColor)
        }
    }

    private fun initActionBtn(text: String?) {
        if (text == null) {
            binding.btnAction.isVisible = false
            return
        }

        binding.btnAction.apply {
            isVisible = true
            setOnClickListener {
                actionUrl?.let {
                    actionClickCallback?.invoke(it)
                }
            }
            this.text = text
        }
    }
}
