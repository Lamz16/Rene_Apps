package com.lamz.reneapps.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lamz.reneapps.data.ResultState
import com.lamz.reneapps.data.UserRepository
import com.lamz.reneapps.response.MapsResponse
import kotlinx.coroutines.launch

class MapsViewModel(private val repository: UserRepository) : ViewModel() {

    fun getSession() = repository.getSession().asLiveData()

    private val _location = MutableLiveData<ResultState<MapsResponse>>()
    val location: LiveData<ResultState<MapsResponse>> = _location

    fun getStoriesWithLocation(token: String) {
        viewModelScope.launch {
            repository.getStoriesWithLocation(token).asFlow().collect{
                _location.value = it
            }
        }

    }
}