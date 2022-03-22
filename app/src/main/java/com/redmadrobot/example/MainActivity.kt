package com.redmadrobot.example

import android.app.ActivityOptions
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cache.StoriesCacheFactory
import cache.StoriesConfig
import com.redmadrobot.example.api.GetStoriesUseCase
import com.redmadrobot.example.databinding.ActivityMainBinding
import com.redmadrobot.stories.models.StoriesInputParams
import com.redmadrobot.stories.stories.adapter.StoriesBasePreviewAdapter
import com.redmadrobot.stories.stories.adapter.StoriesPreviewAdapter
import com.redmadrobot.stories.utils.AnimationUtils
import com.redmadrobot.stories.utils.HorizontalMarginItemDecoration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), StoriesBasePreviewAdapter.StoriesAdapterListener {

    private val storiesAdapter by lazy {
        StoriesPreviewAdapter(this@MainActivity)
    }

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

        binding.recyclerStories.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = storiesAdapter

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

    override fun onStoryClicked(storiesInputParams: StoriesInputParams) {
        AnimationUtils.setExitTransition(this, R.transition.stories_transition)
        val intent = StoriesActivity.newIntent(
            context = this,
            storiesInputParams = storiesInputParams
        )
        val options = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
        startActivity(intent, options)
    }
}
