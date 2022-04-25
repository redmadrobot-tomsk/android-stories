package com.redmadrobot.stories.stories.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cache.StoriesCacheFactory
import com.redmadrobot.stories.models.StoriesInputParams
import com.redmadrobot.stories.models.Story
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Base class for stories recycler view
 * Extend from this class to bind your own cards layout
 *
 * [cache.StoriesCacheFactory] must be initialized before this class created
 */
abstract class StoriesBasePreviewAdapter(
    @LayoutRes private val layout: Int,
    private val inputParams: StoriesInputParams
) : ListAdapter<Story, StoriesBasePreviewAdapter.StoriesBasePreviewViewHolder>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean =
                oldItem == newItem
        }
    }

    private val storage = StoriesCacheFactory.getInstance()
    private val data = MutableLiveData<List<Story>>()
    private val observer = Observer<List<Story>> {
        submitList(it)
    }

    init {
        data.observeForever(observer)
        storage.getScope().launch {
            storage.get(inputParams.storyConfig).collect {
                data.postValue(it)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StoriesBasePreviewViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(layout, parent, false)

        return createViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoriesBasePreviewViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        data.removeObserver(observer)
        super.onDetachedFromRecyclerView(recyclerView)
    }

    abstract fun createViewHolder(view: View): StoriesBasePreviewViewHolder

    abstract class StoriesBasePreviewViewHolder(
        listener: StoriesAdapterListener,
        containerView: View,
        inputParams: StoriesInputParams
    ) : RecyclerView.ViewHolder(containerView) {

        init {
            containerView.setOnClickListener {
                listener.onStoryClicked(
                    inputParams.copy(startStoryPosition = bindingAdapterPosition)
                )
            }
        }

        abstract fun bind(data: Story)
    }

    fun interface StoriesAdapterListener {
        fun onStoryClicked(storiesInputParams: StoriesInputParams)
    }
}
