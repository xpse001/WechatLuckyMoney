package com.example.myapplication.user.data.model.repository

import com.example.myapplication.user.data.model.api.ApiResult
import com.example.myapplication.user.data.model.api.UserApiService
import com.example.myapplication.user.domain.model.User
import javax.inject.Inject


//构造器注入
class UserRepository @Inject constructor(
    private val userApi: UserApiService
) {
    suspend fun fetchUser(userId: Int): ApiResult<User> {
        try {
            val response = userApi.getUser()
            var data = response.getData()
            if (response.isSuccess()) {
                return ApiResult.success(
                    User(
                        userId = data?.id,
                        userName = data?.cdnUrl,
                    )
                )
            }
            return response.getMessage()?.let { ApiResult.error(it) }!!
        } catch (ex: Exception) {
            return ex.message?.let { ApiResult.error(it) }!!
        }
    }


}