package com.example.myapplication.user.data.model.api

class ApiResult<T>(
    private val code: Int,
    private val message: String?,
    private val data: T? // 直接使用类泛型 T
) {

    // 可选：添加 getter 方法
    fun getCode(): Int = code
    fun getMessage(): String? = message
    fun getData(): T? = data


    fun isSuccess(): Boolean = 0 == code

    // 伴生对象
    companion object {

        // 静态工厂方法：创建成功的 ApiResult
        @JvmStatic
        fun <T> success(data: T): ApiResult<T> {
            return ApiResult(code = 0, message = "Success", data = data)
        }

        // 静态工厂方法：创建失败的 ApiResult
        @JvmStatic
        fun <T> error(message: String): ApiResult<T> {
            return ApiResult(code = 500, message = message, data = null)
        }
    }


}

