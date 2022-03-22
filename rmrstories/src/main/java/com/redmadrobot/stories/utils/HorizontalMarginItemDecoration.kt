package com.redmadrobot.stories.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalMarginItemDecoration(
    private val horizontalMargin: Int,
    private val verticalMargin: Int,
    private val firstMarginStart: Int,
    private val lastMarginEnd: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val itemPosition = parent.getChildAdapterPosition(view)

        if (itemPosition == RecyclerView.NO_POSITION) {
            return
        }

        outRect.top = verticalMargin
        outRect.bottom = verticalMargin

        outRect.left = when (itemPosition) {
            0 -> firstMarginStart
            else -> horizontalMargin / 2
        }

        outRect.right = when {
            state.itemCount > 0 && itemPosition == state.itemCount - 1 -> lastMarginEnd
            else -> horizontalMargin / 2
        }
    }
}
