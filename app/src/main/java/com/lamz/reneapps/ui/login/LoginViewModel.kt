package com.lamz.reneapps.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.lamz.reneapps.data.ResultState
import com.lamz.reneapps.data.UserRepository
import com.lamz.reneapps.data.pref.UserModel
import com.lamz.reneapps.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    private val _login = MutableLiveData<ResultState<LoginResponse>>()
    val login: LiveData<ResultState<LoginResponse>> = _login

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun getData(name: String, password: String) {
        viewModelScope.launch {
            repository.loginAccount(name, password).asFlow().collect {
                _login.value = it
            }
        }
    }
}