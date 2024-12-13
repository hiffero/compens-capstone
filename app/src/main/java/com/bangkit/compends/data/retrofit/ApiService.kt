package com.bangkit.compends.data.retrofit

import com.bangkit.compends.data.response.EmployeeResponseItem
import com.bangkit.compends.data.response.LoginRequest
import com.bangkit.compends.data.response.LoginResponse
import com.bangkit.compends.data.response.MessageResponse
import com.bangkit.compends.data.response.PredictRequest
import com.bangkit.compends.data.response.PredictResponse
import com.bangkit.compends.data.response.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @POST("auth/login")
    fun login(
        @Body request: LoginRequest
    ):Call<LoginResponse>

    @POST("auth/register")
    fun register(
        @Body request: RegisterRequest
    ):Call<MessageResponse>

    @POST("/predict")
    fun predict(
        @Body request: PredictRequest
    ): Call<PredictResponse>

    @GET("/employees")
    fun getEmployees(): Call<List<EmployeeResponseItem>>

    @POST("employees/add")
    fun addEmployee(@Body request: EmployeeResponseItem): Call<MessageResponse>

    @DELETE("/employees/{employee_id}")
    fun deleteEmployee(@Path("employee_id") id: String): Call<MessageResponse>
}