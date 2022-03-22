package com.redmadrobot.stories.stories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cache.StoriesCacheFactory
import cache.StoriesConfig
import com.redmadrobot.stories.models.Story
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class StoriesViewModel(private val config: StoriesConfig? = null) : ViewModel() {

    private val storiesController = StoriesCacheFactory.getInstance()

    private val _stories = MutableLiveData<List<Story>>()
    val stories: LiveData<List<Story>> get() = _stories

    val storiesEager get() = _stories.value ?: listOf()

    init {
        getStories()
    }

    fun updateStoryIsSeen(position: Int) {
        viewModelScope.launch {
            storiesController.update(listOf(storiesEager[position].copy(isSeen = true)))
        }
    }

    private fun getStories() {
        viewModelScope.launch {
            storiesController.get(config).take(1).collect {
                _stories.value = it
            }
        }
    }
}
