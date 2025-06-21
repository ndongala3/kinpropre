package com.example.kinpropre.Data

import retrofit2.Response // <--- Assurez-vous que c'est celui de Retrofit
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody // <--- AJOUTEZ CET IMPORT
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("/submit")
    suspend fun uploadData(
        @Part photo: MultipartBody.Part,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody
    ): Response<ResponseBody>
}