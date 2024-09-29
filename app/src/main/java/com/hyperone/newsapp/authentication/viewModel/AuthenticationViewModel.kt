package com.hyperone.newsapp.authentication.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyperone.newsapp.authentication.model.UserModel
import com.hyperone.newsapp.authentication.repo.UserRepo
import com.hyperone.newsapp.network.DataHandler
import com.hyperone.newsapp.utils.SharedPreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val repo: UserRepo,
    private val sharedPreference: SharedPreferenceManager,
) : ViewModel() {

    private val _registrationResult = MutableLiveData<DataHandler<String>>()
    val registrationResult: LiveData<DataHandler<String>> = _registrationResult

    private val _loginResult = MutableLiveData<DataHandler<UserModel>>()
    val loginResult: LiveData<DataHandler<UserModel>> = _loginResult

    fun registerUser(username: String, email: String, password: String) {
        println("userName: $username")
        println("email: $email")
        println("password: $password")
        val newUser = UserModel(userName = username, email = email, password = password)

        println("newUser: $newUser")
        _registrationResult.postValue(DataHandler.ShowLoading) // Show loading state

        viewModelScope.launch {
            val result = repo.registerUser(newUser)
            _registrationResult.postValue(DataHandler.HideLoading) // Hide loading state
            _registrationResult.postValue(result) // Post result (success or error)
        }
    }

    fun loginUser(email: String, password: String) {
        _loginResult.postValue(DataHandler.ShowLoading)

        viewModelScope.launch {
            val result = repo.loginUser(email, password)
            _loginResult.postValue(DataHandler.HideLoading)

            result.let {
                _loginResult.postValue(it)
                if (it is DataHandler.Success) {
                    sharedPreference.saveUser(it.data) // Save user on successful login
                    sharedPreference.setLoggedInFLag(true)
                }
            }
        }
    }
}