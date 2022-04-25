package com.redmadrobot.stories.stories.adapter

import android.graphics.Color
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.palette.graphics.Palette
import com.redmadrobot.stories.R
import com.redmadrobot.stories.databinding.ItemPreviewStoryBinding
import com.redmadrobot.stories.models.StoriesInputParams
import com.redmadrobot.stories.models.Story
import com.redmadrobot.stories.utils.StoriesColorUtils
import com.redmadrobot.stories.utils.setImageWithGlide


/**
 * Implementation of [StoriesBasePreviewAdapter] for displaying stories' previews.
 *
 * @see StoriesBasePreviewAdapter
 * */
class StoriesPreviewAdapter(
    private val listener: StoriesAdapterListener,
    private val inputParams: StoriesInputParams = StoriesInputParams.createDefaults()
) : StoriesBasePreviewAdapter(R.layout.item_preview_story, inputParams) {

    override fun createViewHolder(view: View): StoriesBasePreviewViewHolder =
        StoriesPreviewViewHolder(view, listener, inputParams)

    class StoriesPreviewViewHolder(
        view: View,
        listener: StoriesAdapterListener,
        inputParams: StoriesInputParams
    ) : StoriesBasePreviewViewHolder(listener, view, inputParams) {
        private val binding = ItemPreviewStoryBinding.bind(view)

        override fun bind(data: Story) = with(binding) {
            textTitle.text = data.title
            imgPreview.setImageWithGlide(
                imageUrl = data.previewUrl,
                onReady = { drawable ->
                    drawable ?: return@setImageWithGlide

                    Palette.from(drawable.toBitmap()).generate {
                        val swatch = it?.dominantSwatch ?: return@generate
                        textTitle.setTextColor(
                            if (StoriesColorUtils.isDark(swatch.rgb)) Color.BLACK else Color.WHITE
                        )
                    }
                },
                onFailed = {
                    textTitle.setTextColor(Color.BLACK)
                }
            )
            viewSeen.isVisible = data.isSeen
        }
    }
}
