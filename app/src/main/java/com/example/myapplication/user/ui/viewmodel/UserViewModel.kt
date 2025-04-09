package com.example.myapplication.user.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.user.data.model.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// ViewModel
@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val userTag: String = "user"

    fun fetchUser(userId: Int) {
        viewModelScope.launch {  // 自动绑定 ViewModel 生命周期
            try {
                var fetchUser = userRepository.fetchUser(userId)
                Log.w(userTag, "数据获取成功 ${fetchUser.getData()?.userName}")
            } catch (e: Exception) {
                Log.e(userTag, "请求失败: ${e.message}", e)
            }
        }
    }
}