package com.redmadrobot.example

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cache.StoriesCacheFactory
import cache.StoriesConfig
import com.redmadrobot.example.api.GetStoriesUseCase
import com.redmadrobot.example.custom.CustomStoriesActivity
import com.redmadrobot.example.databinding.ActivityMainBinding
import com.redmadrobot.stories.models.StoriesInputParams
import com.redmadrobot.stories.stories.adapter.StoriesPreviewAdapter
import com.redmadrobot.stories.utils.AnimationUtils
import com.redmadrobot.stories.utils.HorizontalMarginItemDecoration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initStories()
    }

    private fun initStories() {
        val controller = StoriesCacheFactory
            .init(application, CoroutineScope(Dispatchers.IO + SupervisorJob()))
            .preloadImages(true)
            .setConfig(StoriesConfig.All)
            .getInstance()

        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            GetStoriesUseCase().getStories().collect {
                it.onSuccess { stories ->
                    controller.clearAndAdd(StoriesConfig.All, stories)
                }
            }
        }

        // Default story frame impl.
        initPreviewRecycler(binding.recyclerStories) { storiesInputParams ->
            val intent = StoriesActivity.newIntent(
                context = this,
                storiesInputParams = storiesInputParams
            )
            openStoriesActivityAnimated(intent)
        }
        // Custom story frame impl.
        initPreviewRecycler(binding.recyclerCustomStories) { storiesInputParams ->
            val intent = CustomStoriesActivity.newIntent(
                context = this,
                storiesInputParams = storiesInputParams
            )
            openStoriesActivityAnimated(intent)
        }
    }

    private fun initPreviewRecycler(
        recycler: RecyclerView,
        onStoryClicked: (StoriesInputParams) -> Unit
    ) {
        recycler.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = StoriesPreviewAdapter(onStoryClicked)

            val horizontalMargin =
                resources.getDimensionPixelOffset(R.dimen.stories_preview_horizontal_margin)
            val verticalMargin =
                resources.getDimensionPixelOffset(R.dimen.stories_preview_vertical_margin)

            addItemDecoration(
                HorizontalMarginItemDecoration(
                    horizontalMargin = horizontalMargin,
                    verticalMargin = verticalMargin,
                    firstMarginStart = horizontalMargin * 2,
                    lastMarginEnd = horizontalMargin * 2
                )
            )
        }
    }

    private fun openStoriesActivityAnimated(intent: Intent) {
        AnimationUtils.setExitTransition(this, R.transition.stories_transition)
        val options = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
        startActivity(intent, options)
    }
}
