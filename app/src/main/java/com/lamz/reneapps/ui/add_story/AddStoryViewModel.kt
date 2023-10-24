package com.lamz.reneapps.ui.add_story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lamz.reneapps.data.ResultState
import com.lamz.reneapps.data.UserRepository
import com.lamz.reneapps.data.pref.UserModel
import com.lamz.reneapps.response.UploadRegisterResponse
import kotlinx.coroutines.launch
import java.io.File

class AddStoryViewModel(private val repository: UserRepository) : ViewModel() {

    private val _upload = MutableLiveData<ResultState<UploadRegisterResponse>>()
    val upload: LiveData<ResultState<UploadRegisterResponse>> = _upload
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    // with location
    fun uploadImage(token: String, file: File, description: String, lat : String, lon : String) {
        viewModelScope.launch {
            repository.uploadImage(token, file, description, lat, lon).asFlow().collect {
                _upload.value = it
            }
        }
    }

    // without location
    fun uploadImage(token: String, file: File, description: String) {
        viewModelScope.launch {
            repository.uploadImage(token, file, description).asFlow().collect {
                _upload.value = it
            }
        }
    }
}