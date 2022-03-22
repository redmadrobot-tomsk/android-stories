package com.redmadrobot.example.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.redmadrobot.stories.models.Story
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GetStoriesUseCase {

    private val gson: Gson = GsonBuilder().create()

    private val api = Retrofit.Builder()
        .client(OkHttpClient())
        .baseUrl("https://demo.dev.kode-t.ru/mobile/api/v1/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(StoriesApi::class.java)

    fun getStories(): Flow<Result<List<Story>>> = flow {
        emit(Result.success(api.getStories().toStories()))
    }
}
