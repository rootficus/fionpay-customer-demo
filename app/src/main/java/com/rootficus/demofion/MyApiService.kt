package com.rootficus.demofion

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface MyApiService {
    // Define your API endpoints
    @Headers("Content-Type:application/json")
    @POST("api/v2/secure/payment_requests")
    suspend fun fetchData(@Body requestBody: YourRequestBody): YourResponseModel
}