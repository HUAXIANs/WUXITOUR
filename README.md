# 无锡旅游App开发文档

## 项目概述

无锡旅游App是一款基于Android平台的旅游应用，采用Jetpack Compose构建现代化的用户界面，为用户提供无锡地区的旅游信息、行程规划、语音导览等功能。

## 技术架构

### 开发环境
- **开发语言**: Kotlin
- **UI框架**: Jetpack Compose
- **架构模式**: MVVM (Model-View-ViewModel)
- **依赖注入**: 手动依赖注入
- **状态管理**: StateFlow + Compose State

### 主要依赖库
```gradle
// Compose相关
implementation "androidx.compose.ui:ui:$compose_version"
implementation "androidx.compose.material3:material3:$material3_version"
implementation "androidx.activity:activity-compose:$activity_compose_version"

// ViewModel和LiveData
implementation "androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version"

// Navigation
implementation "androidx.navigation:navigation-compose:$nav_version"

// 网络请求
implementation "com.squareup.retrofit2:retrofit:$retrofit_version"
implementation "com.squareup.retrofit2:converter-gson:$retrofit_version"
```

## 项目结构

```
app/src/main/java/com/example/wuxitour/
├── data/                          # 数据层
│   ├── api/                       # API接口定义
│   ├── model/                     # 数据模型
│   └── repository/                # 数据仓库
├── ui/                           # UI层
│   ├── components/               # 通用UI组件
│   ├── screens/                  # 页面组件
│   │   ├── home/                # 首页
│   │   ├── attractions/         # 景点页面
│   │   ├── trip/                # 行程页面
│   │   ├── profile/             # 个人中心
│   │   ├── guide/               # 语音导览
│   │   ├── detail/              # 详情页面
│   │   └── splash/              # 启动页面
│   └── theme/                   # 主题配置
├── utils/                       # 工具类
├── navigation/                  # 导航配置
├── MainActivity.kt             # 主Activity
└── WuxiTourApplication.kt      # 应用程序类
```

## 核心功能模块

### 1. 首页模块 (HomeScreen)
- **功能**: 展示无锡旅游概览、天气信息、热门景点推荐
- **组件**: 
  - 天气卡片
  - 轮播图
  - 分类导航
  - 热门景点列表
  - 精彩活动推荐

### 2. 景点模块 (AttractionsScreen)
- **功能**: 景点搜索、分类浏览、详情查看
- **特性**:
  - 实时搜索功能
  - 分类筛选
  - 景点评分和价格显示
  - 收藏和加入行程功能

### 3. 行程管理模块 (TripScreen)
- **功能**: 行程创建、编辑、智能规划
- **特性**:
  - 手动创建行程
  - 智能路线推荐
  - 行程状态管理
  - 多天行程规划

### 4. 个人中心模块 (ProfileScreen)
- **功能**: 用户管理、收藏、足迹、设置
- **特性**:
  - 用户注册登录
  - 收藏景点管理
  - 游览足迹记录
  - 个人信息设置

### 5. 语音导览模块 (GuideScreen)
- **功能**: 景点语音讲解、播放控制
- **特性**:
  - 多景点导览内容
  - 播放进度控制
  - 导览下载功能
  - 分类浏览

## 数据模型设计

### 景点模型 (Attraction)
```kotlin
data class Attraction(
    val id: String,
    val name: String,
    val description: String,
    val detailedDescription: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val category: String,
    val tags: List<String>,
    val rating: Float,
    val reviewCount: Int,
    val ticketPrice: Double,
    val openingHours: String,
    val phone: String?,
    val website: String?,
    val images: List<String>,
    val isHot: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)
```

### 用户模型 (User)
```kotlin
data class User(
    val id: String,
    val username: String,
    val email: String,
    val phone: String?,
    val avatar: String?,
    val nickname: String,
    val gender: String?,
    val birthday: Long?,
    val location: String?,
    val bio: String?,
    val preferences: List<String>,
    val isVip: Boolean,
    val points: Int,
    val level: Int,
    val createdAt: Long,
    val updatedAt: Long
)
```

### 行程模型 (Trip)
```kotlin
data class Trip(
    val id: String,
    val userId: String,
    val name: String,
    val description: String?,
    val startDate: Long,
    val endDate: Long,
    val attractions: List<TripAttraction>,
    val totalDays: Int,
    val estimatedCost: Double,
    val status: TripStatus,
    val isPublic: Boolean,
    val coverImage: String?,
    val createdAt: Long,
    val updatedAt: Long
)
```

## UI设计规范

### 主题配置
- **主色调**: Material Design 3 蓝色系
- **辅助色**: 青色系
- **支持**: 浅色/深色主题切换
- **字体**: 系统默认字体

### 组件库
- **LoadingIndicator**: 加载状态指示器
- **EmptyState**: 空状态展示
- **ErrorState**: 错误状态展示
- **GradientCard**: 渐变背景卡片
- **RatingStars**: 评分星星组件
- **TagChip**: 标签组件
- **PriceTag**: 价格标签

### 动画效果
- **启动动画**: Logo缩放和渐变效果
- **页面切换**: 滑入滑出动画
- **按钮交互**: 脉冲动画效果
- **列表加载**: 渐入动画

## 开发规范

### 代码规范
1. 使用Kotlin官方代码风格
2. 函数和变量使用驼峰命名
3. 常量使用大写下划线命名
4. 类名使用帕斯卡命名

### 文件组织
1. 按功能模块组织文件结构
2. 每个Screen对应一个ViewModel
3. 通用组件放在components目录
4. 工具类放在utils目录

### 注释规范
1. 类和重要函数添加KDoc注释
2. 复杂逻辑添加行内注释
3. 数据模型添加字段说明

## 测试策略

### 单元测试
- ViewModel逻辑测试
- 工具类函数测试
- 数据模型验证测试

### UI测试
- 页面渲染测试
- 用户交互测试
- 导航流程测试

### 集成测试
- API接口测试
- 数据流测试
- 端到端功能测试

## 性能优化

### 内存优化
- 使用LazyColumn进行列表虚拟化
- 图片懒加载和缓存
- 及时释放不需要的资源

### 网络优化
- 请求缓存策略
- 图片压缩和格式优化
- 分页加载数据

### UI优化
- 减少重组次数
- 使用remember缓存计算结果
- 合理使用State和StateFlow

## 部署说明

### 构建配置
```gradle
android {
    compileSdk 34
    
    defaultConfig {
        applicationId "com.example.wuxitour"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }
    
    buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

### 发布流程
1. 代码审查和测试
2. 版本号更新
3. 签名配置
4. 生成发布APK
5. 应用商店上传

## 后续优化方向

### 功能扩展
1. 地图集成和导航
2. 社交分享功能
3. 离线地图下载
4. AR导览功能
5. 多语言支持

### 技术优化
1. 数据库本地缓存
2. 推送通知
3. 性能监控
4. 崩溃分析
5. A/B测试

## 联系信息

- **项目名称**: 无锡旅游App
- **开发团队**: 杨晔
- **技术支持**: 基于Android Jetpack Compose
- **更新时间**: 2025年6月

---

*本文档将随着项目开发进度持续更新*

