package com.redmadrobot.stories.utils

import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils

object StoriesColorUtils {
    private const val DARK_LUMINANCE = 0.5

    fun isDark(@ColorInt color: Int): Boolean =
        ColorUtils.calculateLuminance(color) > DARK_LUMINANCE
}
