package com.bangkit.compends.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bangkit.compends.data.preference.UserPreference
import com.bangkit.compends.data.response.PredictRequest
import com.bangkit.compends.data.response.PredictResponse
import com.bangkit.compends.data.retrofit.ApiConfig
import com.bangkit.compends.viewmodel.UserViewModel.Companion.TAG
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PredictViewModel(userPreference: UserPreference): ViewModel() {

    fun predict(
        request: PredictRequest,
        onSuccess: (PredictResponse) -> Unit,
        onFailure: (String) -> Unit,
        onLoading: (Boolean) -> Unit
    ){
        onLoading(true)
        val client = ApiConfig().getApiService().predict(request)
        client.enqueue(object : Callback<PredictResponse> {
            override fun onResponse(
                call: Call<PredictResponse>,
                response: Response<PredictResponse>
            ) {
                onLoading(false)
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    onSuccess(responseBody)
                }
            }

            override fun onFailure(call: Call<PredictResponse>, t: Throwable) {
                onLoading(false)
                val errorMsg = t.message ?: "Unknown error occurred"
                onFailure(errorMsg)
                Log.e(TAG, "onFailure: $errorMsg")
            }
        })
    }
}