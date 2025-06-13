package com.example.wuxitour

import android.app.Application
import com.example.wuxitour.utils.Logger
import com.example.wuxitour.utils.ImageUtils
import com.example.wuxitour.utils.NetworkUtils
import com.example.wuxitour.BuildConfig
import com.amap.api.maps.MapsInitializer
import com.amap.api.navi.NaviSetting
import com.amap.api.services.core.ServiceSettings

/**
 * 应用程序类
 */
class WuxiTourApplication : Application() {
    
    private var isInitialized = false
    
    override fun onCreate() {
        super.onCreate()
        
        try {
            // 初始化日志
            Logger.setDebugMode(enabled = BuildConfig.DEBUG)
            Logger.i("WuxiTour Application started")
            
            // 初始化图片工具类
            ImageUtils.init(this)
            
            // 初始化网络状态监听
            NetworkUtils.init(this)
            
            // 初始化高德地图SDK
            initAmapSDK()
            
            isInitialized = true
            Logger.i("应用程序初始化完成")
        } catch (e: Exception) {
            Logger.e("应用程序初始化失败", e)
            isInitialized = false
        }
    }
    
    private fun initAmapSDK() {
        try {
            // 初始化地图
            MapsInitializer.updatePrivacyShow(this, true, true)
            MapsInitializer.updatePrivacyAgree(this, true)
            
            // 初始化导航
            NaviSetting.updatePrivacyShow(this, true, true)
            NaviSetting.updatePrivacyAgree(this, true)
            
            // 初始化搜索
            ServiceSettings.updatePrivacyShow(this, true, true)
            ServiceSettings.updatePrivacyAgree(this, true)
            
            Logger.i("高德地图SDK初始化成功")
        } catch (e: Exception) {
            Logger.e("高德地图SDK初始化失败: ${e.message}")
            throw e // 重新抛出异常，因为地图SDK是核心功能
        }
    }
    
    override fun onLowMemory() {
        super.onLowMemory()
        // 清理图片缓存
        ImageUtils.clearCache()
        Logger.i("内存不足，已清理图片缓存")
    }
    
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        when (level) {
            TRIM_MEMORY_RUNNING_CRITICAL,
            TRIM_MEMORY_RUNNING_LOW,
            TRIM_MEMORY_RUNNING_MODERATE -> {
                ImageUtils.clearCache()
                Logger.i("内存不足，已清理图片缓存")
            }
        }
    }
    
    fun isAppInitialized(): Boolean = isInitialized
}

