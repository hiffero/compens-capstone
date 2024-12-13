package com.bangkit.compends.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.compends.data.preference.UserPreference
import com.bangkit.compends.data.response.EmployeeResponseItem
import com.bangkit.compends.data.response.MessageResponse
import com.bangkit.compends.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmployeeViewModel(private val userPreference: UserPreference) : ViewModel() {
    private val _employees = MutableLiveData<List<EmployeeResponseItem>?>()
    val employees: LiveData<List<EmployeeResponseItem>?> = _employees

    fun getEmployees(
        onSuccess: (List<EmployeeResponseItem>) -> Unit,
        onFailure: (String) -> Unit,
        onLoading: (Boolean) -> Unit
    ) {
        onLoading(true)
        val client = ApiConfig().getApiService().getEmployees()
        client.enqueue(object : Callback<List<EmployeeResponseItem>> {
            override fun onResponse(
                call: Call<List<EmployeeResponseItem>>,
                response: Response<List<EmployeeResponseItem>>
            ) {
                onLoading(false)
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    // Sanitize data before passing it to the UI
                    val sanitizedList = responseBody.map {
                        EmployeeResponseItem(
                            id = it.id,
                            name = it.name?.ifEmpty { "Unknown" },
                            gender = it.gender?.ifEmpty { "Unknown" },
                            age = it.age ?: 0, // Default to 0 if null or empty
                            jobTitle = it.jobTitle?.ifEmpty { "Unknown" },
                            salary = it.salary ?: 0 // Default to 0 if null or empty
                        )
                    }
                    onSuccess(sanitizedList)
                    _employees.value = sanitizedList
                } else {
                    val errorMsg = "Error: ${response.code()} - ${response.message()}"
                    onFailure(errorMsg)
                    Log.e(TAG, "onResponse: $errorMsg")
                }
            }

            override fun onFailure(call: Call<List<EmployeeResponseItem>>, t: Throwable) {
                onLoading(false)
                val errorMsg = t.message ?: "Unknown error occurred"
                onFailure(errorMsg)
                Log.e(TAG, "onFailure: $errorMsg")
            }
        })
    }
    
    fun addEmployee(
        request: EmployeeResponseItem,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
        onLoading: (Boolean) -> Unit
    ){
        onLoading(true)
        val client = ApiConfig().getApiService().addEmployee(request)
        client.enqueue(object : Callback<MessageResponse> {
            override fun onResponse(
                call: Call<MessageResponse>,
                response: Response<MessageResponse>
            ) {
                onLoading(false)
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    onSuccess()
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                onLoading(false)
                val errorMsg = t.message ?: "Unknown error occurred"
                onFailure(errorMsg)
                Log.e(UserViewModel.TAG, "onFailure: $errorMsg")
            }
        })
    }

    fun deleteEmployee(
        id: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
        onLoading: (Boolean) -> Unit
    ){
        onLoading(true)
        val client = ApiConfig().getApiService().deleteEmployee(id)
        client.enqueue(object : Callback<MessageResponse> {
            override fun onResponse(
                call: Call<MessageResponse>,
                response: Response<MessageResponse>
            ) {
                onLoading(false)
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    onSuccess()
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                onLoading(false)
                val errorMsg = t.message ?: "Unknown error occurred"
                onFailure(errorMsg)
                Log.e(UserViewModel.TAG, "onFailure: $errorMsg")
            }
        })
    }

    companion object {
        private const val TAG = "EmployeeViewModel"
    }
}
