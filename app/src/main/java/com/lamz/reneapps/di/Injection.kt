package com.lamz.reneapps.di

import android.content.Context
import com.lamz.reneapps.api.ApiConfig
import com.lamz.reneapps.data.UserRepository
import com.lamz.reneapps.data.database.StoryDatabase
import com.lamz.reneapps.data.pref.UserPreference
import com.lamz.reneapps.data.pref.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val storyDatabase = StoryDatabase.getDatabase(context)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(storyDatabase,pref, apiService)
    }
}