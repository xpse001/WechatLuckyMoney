package com.example.myapplication.user.data

import com.example.myapplication.user.data.model.api.UserApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    // 提供 Retrofit 实例
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://xxx:8008/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 添加 @JvmSuppressWildcards 注解
    @Provides
    @Singleton
    inline fun <reified T> provideApiService(retrofit: Retrofit): T {
        return retrofit.create(T::class.java)
    }

    // 显式声明 UserApiService 的绑定
    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService {
        return provideApiService(retrofit) // 调用通用方法
    }


}