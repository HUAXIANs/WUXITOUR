package com.example.wuxitour.utils

/**
 * 网络请求结果封装
 */
sealed class NetworkResult<T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error<T>(val message: String, val code: Int? = null) : NetworkResult<T>()
    class Loading<T> : NetworkResult<T>()
}

/**
 * 常用常量
 */
object Constants {
    // API相关
    const val BASE_URL = "https://api.wuxitour.com/"
    const val API_TIMEOUT = 30L
    
    // 分页相关
    const val PAGE_SIZE = 20
    const val INITIAL_PAGE = 1
    
    // 缓存相关
    const val CACHE_SIZE = 10 * 1024 * 1024L // 10MB
    const val CACHE_MAX_AGE = 60 * 60 * 24 // 1天
    
    // 地图相关
    const val DEFAULT_ZOOM_LEVEL = 15f
    const val WUXI_LATITUDE = 31.4912
    const val WUXI_LONGITUDE = 120.3124
    
    // 文件相关
    const val MAX_IMAGE_SIZE = 5 * 1024 * 1024L // 5MB
    const val ALLOWED_IMAGE_TYPES = "image/jpeg,image/png,image/webp"
    
    // 业务相关
    const val MIN_PASSWORD_LENGTH = 6
    const val MAX_TRIP_DAYS = 30
    const val MAX_ATTRACTIONS_PER_TRIP = 20
    
    // 错误码
    const val ERROR_NETWORK = 1001
    const val ERROR_SERVER = 1002
    const val ERROR_AUTH = 1003
    const val ERROR_VALIDATION = 1004
    const val ERROR_NOT_FOUND = 1005
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

