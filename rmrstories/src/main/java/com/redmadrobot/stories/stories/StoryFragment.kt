package com.redmadrobot.stories.stories

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.redmadrobot.stories.R
import com.redmadrobot.stories.databinding.FragmentStoryBinding
import com.redmadrobot.stories.models.Story
import com.redmadrobot.stories.models.StoryFrame
import com.redmadrobot.stories.models.StoryFrameControlsColor
import com.redmadrobot.stories.stories.views.StoryFrameView
import com.redmadrobot.stories.stories.views.progress.StoriesProgressView

/**
 * Displays one specific story. This fragment contains the main logic
 * for controlling stories, e.g. like, pause progress, story frame change.
 * */
class StoryFragment : Fragment(), StoryListener {

    companion object {
        private const val KEY_STORY = "KEY_STORY"

        fun newInstance(story: Story): Fragment {
            return StoryFragment().apply {
                arguments = bundleOf(
                    KEY_STORY to story
                )
            }
        }
    }

    private var _binding: FragmentStoryBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var actionsCallback: StoryActionsCallback

    private lateinit var story: Story
    private val frames: List<StoryFrame>
        get() = story.frames

    private var currentFrame = 0
    private var isPause = false
    private var isFrameLoaded = false

    private val progressListener = object : StoriesProgressView.StoryProgressListener {
        override fun onNext() = onNextFrame()
        override fun onPrev() = onPreviousFrame()
        override fun onComplete() = actionsCallback.onCompleteStory()
    }

    private val storyFrameListener = object : StoryFrameView.StoryFrameListener {
        override fun onLoaded() {
            isFrameLoaded = true
            binding.progressView.startProgress()
        }

        override fun onNext() = onNextFrame {
            isFrameLoaded = false
            binding.progressView.next()
        }

        override fun onPrev() = onPreviousFrame {
            isFrameLoaded = false
            binding.progressView.previous()
        }

        override fun onPause() = binding.progressView.pause()
        override fun onResume() = binding.progressView.resume()
    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun parseArguments() {
        arguments?.getParcelable<Story>(KEY_STORY)?.let {
            story = it
        } ?: run {
            Log.d("Story", "Story must not be null")
            activity?.finish()
        }
    }

    private fun initViews() {
        binding.progressView.apply {
            storiesCount = frames.size
            storyProgressListener = progressListener
        }

        binding.imgClose.setOnClickListener {
            actionsCallback.closeStories()
        }

        binding.storyFrameView.listener = storyFrameListener

        binding.storyFrameView.setActionCallback { url ->
            StoryIntentUtil.executeStoryAction(requireContext(), url)
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
        binding.storyFrameView.storyFrame = frames.getOrNull(currentFrame)
        binding.progressView.setStartStory(currentFrame)
        if (isFrameLoaded) binding.progressView.startProgress()
        updateStoryControls(frames[currentFrame])
    }

    private fun startLastStoryFrame() {
        val lastStoryFrame = frames[currentFrame]
        binding.progressView.setStartStory(currentFrame)
        binding.storyFrameView.storyFrame = lastStoryFrame
        updateStoryControls(lastStoryFrame)
    }

    private fun onNextFrame(onFrameSwitched: (() -> Unit)? = null) {
        frames.getOrNull(currentFrame + 1)?.apply {
            ++currentFrame
            onFrameSwitched?.invoke()
            binding.storyFrameView.storyFrame = this
            updateStoryControls(this)
        } ?: run {
            actionsCallback.onCompleteStory()
        }
    }

    private fun onPreviousFrame(onFrameSwitched: (() -> Unit)? = null) {
        frames.getOrNull(currentFrame - 1)?.apply {
            --currentFrame
            onFrameSwitched?.invoke()
            binding.storyFrameView.storyFrame = this
            updateStoryControls(this)
        } ?: run {
            if (!actionsCallback.hasPreviousStory(story)) {
                binding.progressView.reverse()
                if (isFrameLoaded) binding.progressView.startProgress()
            }
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
            binding.storyFrameView.listener = storyFrameListener
            binding.progressView.storyProgressListener = progressListener
            binding.progressView.setStartStory(currentFrame)
            if (isFrameLoaded) binding.progressView.startProgress()
        }
    }

    override fun onStop() {
        super.onStop()
        binding.storyFrameView.listener = null
        binding.progressView.storyProgressListener = null
        binding.progressView.destroy()
        isPause = true
    }
}
