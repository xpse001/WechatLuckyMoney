package com.example.myapplication.user.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {


    private val sharedPreferences =
        context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

    fun getSavedText(): String {
        var text = sharedPreferences.getString("saved_text", "1")
        Log.d("SAVE", "getSavedText: $text")
        return text ?: "1"
    }

    fun saveText(text: String) {
        sharedPreferences.edit()
            .putString("saved_text", text)
            .apply()
        Log.d("SAVE", "Saved text: $text")
    }


}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel() // 依赖注入
) {
    // 使用 viewModel 代替手动获取
    val keyboardController = LocalSoftwareKeyboardController.current


// 使用本地状态暂存输入内容
    var localText by remember { mutableStateOf(viewModel.getSavedText()) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = localText,
            label = { Text("红包随机延时时长") },
            onValueChange = { newText -> localText = newText },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            maxLines = 5,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.saveText(localText)
                keyboardController?.hide()
                navController.popBackStack()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("保存")
        }
    }
}