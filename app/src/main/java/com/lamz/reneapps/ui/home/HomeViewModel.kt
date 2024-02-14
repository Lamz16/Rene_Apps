package com.lamz.reneapps.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lamz.reneapps.data.ResultState
import com.lamz.reneapps.data.UserRepository
import com.lamz.reneapps.response.GetListResponse
import com.lamz.reneapps.response.ListStoryItem
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: UserRepository) : ViewModel() {

    val snackbarText = repository.snackbarText


    fun getSession() = repository.getSession().asLiveData()
    private val _story = MutableLiveData<ResultState<GetListResponse>>()
    val story: LiveData<ResultState<GetListResponse>> = _story

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getStories(): LiveData<PagingData<ListStoryItem>> =
        repository.getStories().cachedIn(viewModelScope)
}