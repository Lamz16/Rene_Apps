package com.lamz.reneapps.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lamz.reneapps.data.ResultState
import com.lamz.reneapps.data.UserRepository
import com.lamz.reneapps.data.pref.UserModel
import com.lamz.reneapps.response.DetailResponse
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: UserRepository) : ViewModel() {

    private val _detailStory = MutableLiveData<ResultState<DetailResponse>>()
    val detailStory: LiveData<ResultState<DetailResponse>> = _detailStory

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getDetailStories(id: String) {
        viewModelScope.launch {
            repository.getDetailStories(id).asFlow().collect {
                _detailStory.value = it
            }
        }

    }


}