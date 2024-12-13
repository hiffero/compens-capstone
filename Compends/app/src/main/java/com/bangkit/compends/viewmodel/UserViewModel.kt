package com.bangkit.compends.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bangkit.compends.data.preference.UserPreference
import com.bangkit.compends.data.response.LoginRequest
import com.bangkit.compends.data.response.LoginResponse
import com.bangkit.compends.data.response.MessageResponse
import com.bangkit.compends.data.response.RegisterRequest
import com.bangkit.compends.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel(private val pref: UserPreference) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun getUserToken(): LiveData<String?> {
        return pref.getUser().asLiveData()
    }

    fun saveUserToken(token: String) {
        viewModelScope.launch {
            if (token.isNotBlank()) {
                pref.saveUser(token)
            }
        }
    }

    fun destroyUserToken(
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            pref.destroyUser()
            onSuccess()
        }
    }

    fun login(request: LoginRequest, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        _isLoading.value = true
        val client = ApiConfig().getApiService().login(request)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                _isLoading.value = false
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val userData = responseBody.user
                    if (!userData?.name.isNullOrEmpty()) {
                        viewModelScope.launch {
                            saveUserToken(userData?.name.toString())
                            onSuccess()
                        }
                    } else {
                        val errorMsg = "Invalid token received"
                        _errorMessage.value = errorMsg
                        onFailure(errorMsg)
                        Log.e(TAG, "onResponse Failure: $errorMsg")
                    }
                } else {
                    val errorMsg = response.message() ?: "Login failed"
                    _errorMessage.value = errorMsg
                    onFailure(errorMsg)
                    Log.e(TAG, "onResponse Failure: $errorMsg")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                val errorMsg = t.message ?: "Unknown error occurred"
                _errorMessage.value = errorMsg
                onFailure(errorMsg)
                Log.e(TAG, "onFailure: $errorMsg")
            }
        })
    }


    fun register(
        request: RegisterRequest,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        _isLoading.value = true
        val client = ApiConfig().getApiService().register(request)
        client.enqueue(object : Callback<MessageResponse> {
            override fun onResponse(
                call: Call<MessageResponse>,
                response: Response<MessageResponse>
            ) {
                _isLoading.value = false
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val message = responseBody.message ?: "Registration successful"
                    onSuccess(message)
                } else {
                    val errorMsg = response.message() ?: "Registration failed"
                    _errorMessage.value = errorMsg
                    onFailure(errorMsg)
                    Log.e(TAG, "onResponse Failure: $errorMsg")
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                _isLoading.value = false
                val errorMsg = t.message ?: "Unknown error occurred"
                _errorMessage.value = errorMsg
                onFailure(errorMsg)
                Log.e(TAG, "onFailure: $errorMsg")
            }
        })
    }

    companion object {
        const val TAG = "UserViewModel"
    }
}
