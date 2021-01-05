package com.e.zodiaconv

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface RestApi {

    @Headers("Content-Type: application/json")
    @POST("sign")
    fun addUser(@Body userData: UserInfo): Call<UserInfo>




        @FormUrlEncoded
        @POST("/")
        suspend fun createEmployee(@FieldMap params: HashMap<String?, String?>): Response<ResponseBody>


}