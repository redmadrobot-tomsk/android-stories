package com.redmadrobot.stories.stories

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.redmadrobot.stories.R
import com.redmadrobot.stories.databinding.FragmentStoryBinding
import com.redmadrobot.stories.models.*
import com.redmadrobot.stories.models.exception.StoryInstanceRequired
import com.redmadrobot.stories.stories.views.BaseStoryFrameView
import com.redmadrobot.stories.stories.views.StoryFrameViewImpl
import com.redmadrobot.stories.stories.views.progress.StoriesProgressView

/**
 * Displays one specific [Story]. This fragment contains the main logic
 * for controlling stories, e.g. like, pause progress, story frame change.
 *
 * Extend this class to use custom [BaseStoryFrameView]
 * by overriding [createStoryFrameView]
 * and providing your [BaseStoryFrameView] implementation like this:
 *
 * ```
 * class MyStoryFragmentImpl: StoryFragment() {
 *     companion object {
 *         fun newInstance(story: Story): StoryFragment =
 *             MyStoryFragmentImpl().addStoryToArguments(story)
 *     }
 *
 *     override fun createStoryFrameView(context: Context): BaseStoryFrameView {
 *         return MyStoryFrameViewImpl(context)
 *     }
 * }
 * ```
 *
 * Then, don't forget to override StoriesBaseActivity#createStoriesFragment:
 * ```
 * class MyStoriesActivity: StoriesBaseActivity() {
 * ...
 *     override val createStoriesFragment: ((Story) -> StoryFragment)? = { story ->
 *         MyStoryFragmentImpl.newInstance(story)
 *     }
 * ...
 * }
 * ```
 *
 * If [createStoryFrameView] was not overridden,
 * default implementation [StoryFrameViewImpl] will be used.
 *
 * @see [Story], [BaseStoryFrameView], [StoriesBaseActivity].
 * */
open class StoryFragment : Fragment(), StoryListener {

    companion object {
        private const val KEY_STORY = "KEY_STORY"
        private const val KEY_STORY_IS_SEEN_WHEN = "STORY_IS_SEEN_WHEN"

        /**
         * Adds [Story] to bundle of [StoryFragment].
         * Use it to pass [Story] instance when extending [StoryFragment].
         * You MUST pass the story instance, otherwise, [StoryInstanceRequired] will be thrown.
         *
         * @param[story] Story to be displayed.
         *
         * @param[storyIsSeenWhen] Defines when the story is considered "seen"
         * (depending on the frame).
         * Optional parameter. Default value is [StoryIsSeenWhen.LAST_FRAME].
         *
         * @return[StoryFragment] Story fragment bundled with story instance.
         *
         * @throws[StoryInstanceRequired] if this method was not used when creating the derived class.
         * (Exception is thrown during runtime, after the view is created)
         *
         * @see [Story], [StoryIsSeenWhen], [StoryInstanceRequired].
         * */
        @JvmStatic
        protected fun StoryFragment.addStoryToArguments(
            story: Story,
            storyIsSeenWhen: StoryIsSeenWhen? = null
        ): StoryFragment {
            return this.apply {
                arguments = bundleOf(
                    KEY_STORY to story,
                    KEY_STORY_IS_SEEN_WHEN to storyIsSeenWhen
                )
            }
        }

        fun newStoryFragmentInstance(
            story: Story,
            storyIsSeenWhen: StoryIsSeenWhen? = null
        ): StoryFragment {
            return StoryFragment().addStoryToArguments(story, storyIsSeenWhen)
        }
    }

    private var _binding: FragmentStoryBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var storyFrameView: BaseStoryFrameView

    private lateinit var actionsCallback: StoryActionsCallback

    private lateinit var story: Story
    private val frames: List<StoryFrame>
        get() = story.frames
    private lateinit var storyIsSeenWhen: StoryIsSeenWhen

    private var currentFrame = 0
    private var isPause = false
    private var isFrameLoaded = false

    private val progressListener = object : StoriesProgressView.StoryProgressListener {
        override fun onNext() = onNextFrame()
        override fun onPrev() = onPreviousFrame()
        override fun onComplete() = actionsCallback.onCompleteStory()
    }

    private val storyFrameListener = object : BaseStoryFrameView.StoryFrameListener {
        override fun onLoaded() {
            isFrameLoaded = true
            binding.progressView.startProgress()
        }

        override fun onNext() = onNextFrame {
            isFrameLoaded = false
            binding.progressView.next()
            setStoryIsSeen()
        }

        override fun onPrev() = onPreviousFrame {
            isFrameLoaded = false
            binding.progressView.previous()
            setStoryIsSeen()
        }

        override fun onPause() = binding.progressView.pause()
        override fun onResume() = binding.progressView.resume()
    }

    /**
     * Override this method to use your implementation [BaseStoryFrameView].
     * If [createStoryFrameView] was not overridden, [StoryFrameViewImpl] will be used.
     * */
    protected open fun createStoryFrameView(context: Context): BaseStoryFrameView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        actionsCallback = context as StoryActionsCallback
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parseArguments()
        initViews()
    }

    private fun parseArguments() {
        arguments?.getParcelable<Story>(KEY_STORY)?.let {
            story = it
        } ?: throw StoryInstanceRequired
        storyIsSeenWhen = arguments?.getParcelable(KEY_STORY_IS_SEEN_WHEN)
            ?: StoriesInputParams.defaultStoryIsSeenWhen()
    }

    private fun initViews() {
        // Replace default story frame with the custom one if createStoryFrameView was overridden.
        val customView = createStoryFrameView(requireContext())?.also { customView ->
            with(binding.root) {
                customView.layoutParams = binding.storyFrameView.layoutParams
                val storyFrameIndex = indexOfChild(binding.storyFrameView)
                removeView(binding.storyFrameView)
                addView(customView, storyFrameIndex)
            }
        }
        storyFrameView = customView ?: binding.storyFrameView
        storyFrameView.listener = storyFrameListener

        binding.progressView.apply {
            storiesCount = frames.size
            storyProgressListener = progressListener
        }

        binding.imgClose.setOnClickListener {
            actionsCallback.closeStories()
        }
    }

    /**
     * Main function to play story
     */
    override fun startStory(swipeDirection: SwipeDirection) {
        when (swipeDirection) {
            SwipeDirection.RIGHT -> {
                startStoryFrame()
            }
            SwipeDirection.LEFT -> {
                isFrameLoaded = false
                currentFrame = frames.size - 1
                startLastStoryFrame()
            }
        }
    }

    private fun startStoryFrame() {
        storyFrameView.storyFrame = frames.getOrNull(currentFrame)
        binding.progressView.setStartStory(currentFrame)
        if (isFrameLoaded) binding.progressView.startProgress()
        updateStoryControls(frames[currentFrame])
    }

    private fun startLastStoryFrame() {
        val lastStoryFrame = frames[currentFrame]
        binding.progressView.setStartStory(currentFrame)
        storyFrameView.storyFrame = lastStoryFrame
        updateStoryControls(lastStoryFrame)
    }

    private fun onNextFrame(onFrameSwitched: (() -> Unit)? = null) {
        frames.getOrNull(currentFrame + 1)?.apply {
            ++currentFrame
            onFrameSwitched?.invoke()
            storyFrameView.storyFrame = this
            updateStoryControls(this)
        } ?: run {
            actionsCallback.onCompleteStory()
        }
    }

    private fun onPreviousFrame(onFrameSwitched: (() -> Unit)? = null) {
        frames.getOrNull(currentFrame - 1)?.apply {
            --currentFrame
            onFrameSwitched?.invoke()
            storyFrameView.storyFrame = this
            updateStoryControls(this)
        } ?: run {
            if (!actionsCallback.hasPreviousStory(story)) {
                binding.progressView.reverse()
                if (isFrameLoaded) binding.progressView.startProgress()
            }
        }
    }

    private fun setStoryIsSeen() {
        when (storyIsSeenWhen) {
            StoryIsSeenWhen.IMMEDIATE -> if (currentFrame >= 0) actionsCallback.setStorySeen()
            StoryIsSeenWhen.ONE -> if (currentFrame >= 1) actionsCallback.setStorySeen()
            StoryIsSeenWhen.TWO -> if (currentFrame >= 2) actionsCallback.setStorySeen()
            StoryIsSeenWhen.LAST_FRAME -> if (currentFrame == frames.lastIndex) actionsCallback.setStorySeen()
        }
    }

    private fun updateStoryControls(currentFrame: StoryFrame) {
        val controlsColor = ContextCompat.getColor(
            requireContext(),
            when (currentFrame.content.controlsColor) {
                StoryFrameControlsColor.DARK -> R.color.black
                else -> R.color.white
            }
        )

        binding.imgClose.setColorFilter(controlsColor)
        binding.progressView.changeProgressColor(currentFrame.content.controlsColor)
    }

    override fun onPause() {
        super.onPause()
        stopStory()
    }

    override fun stopStory() {
        binding.progressView.pause()
    }

    override fun resumeStory() {
        binding.progressView.resume()
    }

    override fun onStart() {
        super.onStart()
        if (isPause) {
            storyFrameView.listener = storyFrameListener
            binding.progressView.storyProgressListener = progressListener
            binding.progressView.setStartStory(currentFrame)
            if (isFrameLoaded) binding.progressView.startProgress()
        }
    }

    override fun onStop() {
        super.onStop()
        storyFrameView.listener = null
        binding.progressView.storyProgressListener = null
        binding.progressView.destroy()
        isPause = true
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
