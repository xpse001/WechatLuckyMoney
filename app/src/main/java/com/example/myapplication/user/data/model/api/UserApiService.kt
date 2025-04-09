package com.example.myapplication.user.data.model.api

import com.example.myapplication.user.data.model.UserDTO
import retrofit2.http.GET

interface UserApiService {

    /**
     * Retrofit 的 suspend 函数自动在 IO 线程执行
     * ApiResult.success()
     */
    @GET("/common/config")
    suspend fun getUser(): ApiResult<UserDTO>
}