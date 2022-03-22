package com.redmadrobot.stories.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

fun ImageView.setImageWithGlide(
    imageUrl: String,
    onReady: (Drawable?) -> Unit,
    onFailed: () -> Unit
) {
    Glide
        .with(this)
        .load(imageUrl)
        .listener(createDefaultLoadingListener(onReady, onFailed))
        .into(this)
}

private fun createDefaultLoadingListener(
    onReady: ((Drawable?) -> Unit)? = null,
    onFailed: (() -> Unit)? = null
): RequestListener<Drawable> {
    return object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            onFailed?.invoke()
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            onReady?.invoke(resource)
            return false
        }
    }
}
