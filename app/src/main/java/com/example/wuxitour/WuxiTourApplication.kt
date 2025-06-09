package com.example.wuxitour

import android.app.Application
import com.example.wuxitour.utils.Logger
import com.example.wuxitour.BuildConfig



/**
 * 应用程序类
 */
class WuxiTourApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化日志
        Logger.setDebugMode(enabled = BuildConfig.DEBUG)
        Logger.i("WuxiTour Application started")
        
        // 这里可以初始化其他组件
        // 例如：数据库、网络库、图片加载库等
    }
}

