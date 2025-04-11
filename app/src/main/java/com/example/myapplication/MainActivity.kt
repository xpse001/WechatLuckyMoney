package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.user.ui.SettingsScreen
import com.example.myapplication.user.ui.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @SuppressLint("UnrememberedGetBackStackEntry")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {


            MyApplicationTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "main") {
                    composable("main") {
                        Greeting(
                            navController,
                            name = "Android",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    composable("settings") {
                        val backStackEntry =
                            remember { navController.getBackStackEntry("settings") }
                        SettingsScreen(
                            navController = navController,
                            viewModel = hiltViewModel(backStackEntry)
                        )
                    }
                }
//                Greeting(
//                    null,
//                    name = "Android",
//                    modifier = Modifier.fillMaxSize()
//                )


            }
        }
    }
}


@Composable
fun Greeting(
    navController: NavController?,
    name: String,
    modifier: Modifier = Modifier,
    viewModel: UserViewModel = hiltViewModel()
) {
    var TAG = "下载"
    var test by remember {
        mutableStateOf("Android chao")
    }
    var text by remember { mutableStateOf("") }

    //val bgIds = remember { mutableStateListOf(R.drawable.bg1, R.drawable.bg2, R.drawable.bg3) }
    val bgIds = remember { mutableStateListOf(R.drawable.bg1) }
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
        //.systemBarsPadding() // 处理系统栏遮挡内容
    ) {
        items(bgIds) { id ->
            CustomDownloadCard(
                id, Modifier.padding(bottom = 6.dp)
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 其他组件...
        // 2. 悬浮按钮（默认右下角）
        FloatingActionButton(
            onClick = {
                //viewModel.fetchUser(1)
                navController?.navigate("settings")
                Log.w(TAG, "Greeting: ")

            },
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.BottomEnd),
            //shape = CircleShape, // 圆形
            containerColor = Color(0xFF2196F3), // 背景色
            contentColor = Color.White // 图标颜色
        ) {
            Icon(Icons.Default.Settings, "红包设置")
        }
    }


}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting(null, "Android")
    }
}


@Composable
fun CustomDownloadCard(resource: Int = R.drawable.bg1, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(resource),
            contentDescription = "背景图片1",
            contentScale = ContentScale.Crop,
            modifier =
            Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.LightGray)
        )
        Spacer(Modifier.height(24.dp)) // 图标与标题间距
        Text(
            text = "图标自定义下载", style = MaterialTheme.typography.titleLarge.copy(
                color = Color.Black,
                fontWeight = FontWeight.Bold,
            ), modifier = Modifier
                .padding(bottom = 8.dp)


        )
        Text(
            text = "支持AI/SVG/PNG/代码格式下载",
            color = Color(0xFF424040),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(text = "支持按路径在线编辑icon颜色", color = Color(0xFF424040))
    }

}

