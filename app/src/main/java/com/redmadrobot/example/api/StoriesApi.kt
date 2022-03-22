package com.redmadrobot.example.api

import com.redmadrobot.example.api.model.StoriesResponse
import retrofit2.http.GET

interface StoriesApi {

    @GET("stories")
    suspend fun getStories(): StoriesResponse
}
