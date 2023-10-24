package com.lamz.reneapps.di

import android.content.Context
import com.lamz.reneapps.api.ApiConfig
import com.lamz.reneapps.data.UserRepository
import com.lamz.reneapps.data.pref.UserPreference
import com.lamz.reneapps.data.pref.dataStore


object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(pref, apiService)
    }
}