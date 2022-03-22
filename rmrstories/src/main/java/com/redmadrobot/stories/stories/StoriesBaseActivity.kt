package com.redmadrobot.stories.stories

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import cache.StoriesConfig
import com.redmadrobot.stories.R
import com.redmadrobot.stories.databinding.ActivityStoriesBinding
import com.redmadrobot.stories.models.Story
import com.redmadrobot.stories.models.StoriesInputParams
import com.redmadrobot.stories.models.StoriesStartPositionRequired
import com.redmadrobot.stories.utils.AnimationUtils
import draggableview.LockableNestedScrollView

/**
 * Main container for stories. Input parameters are list of stories.
 * Parent view is [ViewPager2].
 *
 * You must start with activity with intent supplied by [newStoriesIntent] method.
 * Otherwise, [StoriesStartPositionRequired] exception will be thrown.
 *
 * To use stories, extend this activity in main module to take full control of stories.
 */
abstract class StoriesBaseActivity :
    FragmentActivity(R.layout.activity_stories),
    StoryActionsCallback,
    LockableNestedScrollView.DraggableViewListener {

    protected companion object {
        private const val KEY_CONFIG = "KEY_CONFIG"
        private const val KEY_START_POSITION = "KEY_START_POSITION"

        private const val START_POSITION_NULL = -1

        /**
         * Returns [Intent] bundled with required parameters for starting [StoriesBaseActivity].
         * Use it to get an intent to start your [StoriesBaseActivity]'s subclass.
         * If activity started without this [Intent], throws [StoriesStartPositionRequired].
         *
         * @param[context] Android context.
         * @param[clazz] [StoriesBaseActivity]'s subclass.
         * @param[storiesInputParams] Input parameters for [StoriesBaseActivity].
         *
         * @return [Intent] with bundled parameters from [StoriesInputParams].
         * @throws [StoriesStartPositionRequired] if activity was started without intent
         * bundled with input parameters ([StoriesInputParams]).
         *
         * @see [StoriesInputParams].
         * */
        fun newStoriesIntent(
            context: Context,
            clazz: Class<out StoriesBaseActivity>,
            storiesInputParams: StoriesInputParams
        ): Intent = Intent(context, clazz)
            .putExtra(KEY_START_POSITION, storiesInputParams.startStoryPosition)
            .putExtra(KEY_CONFIG, storiesInputParams.storyConfig)
    }

    private lateinit var binding: ActivityStoriesBinding

    private var currentStory = -1
    private var direction = SwipeDirection.RIGHT

    private val viewModel by lazy {
        StoriesViewModel(intent.getParcelableExtra(KEY_CONFIG) as StoriesConfig?)
    }
    private val windowInsetsController by lazy {
        ViewCompat.getWindowInsetsController(binding.draggableView)
    }

    /**
     * Main callback for left-right stories swipes. Story starts playing only after this callback
     */
    private val frameChangedCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            getCurrentStory(position)?.startStory(direction)
            currentStory = position
            direction = SwipeDirection.RIGHT
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AnimationUtils.setEnterTransition(this, R.transition.stories_transition)

        windowInsetsController?.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController?.hide(WindowInsetsCompat.Type.systemBars())

        val startPosition = savedInstanceState?.getInt(KEY_START_POSITION, START_POSITION_NULL)
            ?: intent.getIntExtra(KEY_START_POSITION, START_POSITION_NULL)

        // Required parameter that must be bundled with intent.
        if (startPosition == START_POSITION_NULL) throw StoriesStartPositionRequired

        currentStory = startPosition

        setContent()

        binding.draggableView.setDragUpEnabled(false)
        binding.draggableView.setOnDragDismissedListener {
            finishAfterTransition()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setContent() {
        viewModel.stories.observe(this) { stories ->
            binding.viewPagerContainer.apply {
                adapter = StoriesViewPagerAdapter(this@StoriesBaseActivity, ArrayList(stories)) {
                    post {
                        frameChangedCallback.onPageSelected(currentStory)
                    }
                }

                registerOnPageChangeCallback(frameChangedCallback)
                setCurrentItem(currentStory, false)
                /**
                 * preload 1 stories for faster loading
                 * and prevent creating fragment before {@link #frameChangedCallback} worked
                 * @see StoriesViewPagerAdapter
                 */
                offscreenPageLimit = 1

                // set listener to viewpager2 for horizontal scroll
                getChildAt(0).setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_UP) getCurrentStory(currentItem)?.resumeStory()
                    super.onTouchEvent(event)
                }
            }
        }
    }

    private fun getCurrentStory(position: Int): StoryListener? {
        val frgId = binding.viewPagerContainer.adapter?.getItemId(position) ?: return null
        return supportFragmentManager.findFragmentByTag("f$frgId") as? StoryListener
    }

    // region StoryActionsCallback

    override fun closeStories() {
        showSystemBars()
        finishAfterTransition()
    }

    override fun onCompleteStory() {
        viewModel.updateStoryIsSeen(currentStory)
        if (viewModel.storiesEager.size > currentStory + 1) {
            binding.viewPagerContainer.setCurrentItem(++currentStory, true)
        } else {
            showSystemBars()
            finishAfterTransition()
        }
    }

    override fun hasPreviousStory(story: Story): Boolean {
        return if (viewModel.storiesEager.indexOf(story) > 0) {
            direction = SwipeDirection.LEFT
            binding.viewPagerContainer.setCurrentItem(--currentStory, true)
            true
        } else {
            false
        }
    }

    // endregion

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_START_POSITION, currentStory)
        super.onSaveInstanceState(outState)
    }

    override fun onDrag() {
        getCurrentStory(binding.viewPagerContainer.currentItem)?.stopStory()
    }

    override fun onStopDrag() {
        getCurrentStory(binding.viewPagerContainer.currentItem)?.resumeStory()
    }

    private fun showSystemBars() {
        windowInsetsController?.show(WindowInsetsCompat.Type.systemBars())
    }
}
