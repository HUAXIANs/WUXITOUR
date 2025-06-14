package com.example.wuxitour.utils

/**
 * 应用常量
 */
object Constants {
    // 高德地图配置
    const val AMAP_KEY = "b58ae385c94d250003fc87fe4ec6d79d"
    const val AMAP_WEB_SERVICE_KEY = "bcff2b116346619ca46de0837e9c7b68"
    
    // 网络配置
    const val BASE_URL = "https://restapi.amap.com/" // 高德地图Web API地址
    const val NETWORK_TIMEOUT = 30L
    const val NETWORK_RETRY_COUNT = 3
    const val NETWORK_RETRY_DELAY = 1000L // 1秒
    
    // 缓存配置
    const val CACHE_SIZE = 10 * 1024 * 1024L // 10MB
    const val CACHE_DIR = "data_cache"
    
    // 图片配置
    const val IMAGE_CACHE_SIZE = 50 * 1024 * 1024L // 50MB
    const val IMAGE_CACHE_DIR = "image_cache"
    const val IMAGE_COMPRESS_QUALITY = 80
    const val IMAGE_MAX_WIDTH = 1920
    const val IMAGE_MAX_HEIGHT = 1080
    
    // 默认配置
    const val DEFAULT_PAGE_SIZE = 20
    const val DEFAULT_RADIUS = 5000 // meters
    
    // 错误信息
    const val ERROR_NETWORK = "网络连接失败，请检查您的网络设置"
    const val ERROR_LOCATION_PERMISSION_DENIED = "定位权限被拒绝，无法获取您的位置信息"
    const val ERROR_LOADING = "数据加载失败，请稍后再试"
    const val ERROR_NOT_FOUND = "未找到相关数据"
    const val ERROR_UNKNOWN = "发生未知错误"
    const val ERROR_LOGIN_FAILED = "登录失败，请检查用户名和密码"
    const val ERROR_REGISTER_FAILED = "注册失败，请稍后再试"
    const val ERROR_NOT_LOGGED_IN = "用户未登录"
    const val ERROR_LOADING_TRIPS = "行程加载失败，请稍后再试"
}

/**
 * 应用状态
 */
enum class AppState {
    LOADING,
    SUCCESS,
    ERROR,
    EMPTY
}

/**
 * 主题模式
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

/**
 * 语言设置
 */
enum class Language(val code: String, val displayName: String) {
    CHINESE("zh", "中文"),
    ENGLISH("en", "English")
}

