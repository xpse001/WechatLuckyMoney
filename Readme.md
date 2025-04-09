feature_user
├── data
│   ├── UserRepositoryImpl.kt   // 实现数据获取
│   └── model
│       └── UserDto.kt          // 网络数据模型
│
├── domain
│   ├── model
│   │   └── User.kt            // 业务模型
│   └── usecase
│       └── GetUserUseCase.kt  // 业务逻辑
│
└── ui
├── screen
│   ├── UserScreen.kt      // 主界面
│   └── UserViewModel.kt   // 关联的 VM
└── components
    └── UserAvatar.kt      // 复用头像组件


1. 按功能/业务划分包
    优先按功能模块划分（如 user, product），而不是按技术分层（如 activity, adapter）
2. ViewModel 的放置
   推荐与对应 UI 放在同一包下：

3. DTO 与 Domain Model 分离

// data/model/UserDto.kt（网络层模型）
data class UserDto(val id: Int, val name: String)

// domain/model/User.kt（业务层模型）
data class User(val userId: Int, val userName: String)