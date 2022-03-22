package com.redmadrobot.stories.stories

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.redmadrobot.stories.R

object StoryIntentUtil {

    private const val MAILTO_PREFIX = "mailto:"
    private const val MAILTO_SEPARATOR = ":"

    fun executeStoryAction(context: Context, url: String) {

        val intent = when {
            url.startsWith(MAILTO_PREFIX) -> createSendEmailIntent(
                url.substringAfter(MAILTO_SEPARATOR)
            )
            else -> createBrowserIntent(url)
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.apps_not_found),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun createSendEmailIntent(email: String, subject: String = ""): Intent {
        return Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
    }

    private fun createBrowserIntent(url: String): Intent =
        Intent(Intent.ACTION_VIEW, Uri.parse(url))
}
