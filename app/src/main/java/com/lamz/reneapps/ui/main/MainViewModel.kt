package com.lamz.reneapps.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lamz.reneapps.data.ResultState
import com.lamz.reneapps.data.UserRepository
import com.lamz.reneapps.response.GetListResponse
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    fun getSession() = repository.getSession().asLiveData()
    private val _story = MutableLiveData<ResultState<GetListResponse>>()
    val story: LiveData<ResultState<GetListResponse>> = _story

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}