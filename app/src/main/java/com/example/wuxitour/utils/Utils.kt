package com.example.wuxitour.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

/**
 * 日志工具类
 */
object Logger {
    private const val TAG = "WuxiTour"
    private var isDebugMode = true
    
    fun d(message: String, tag: String = TAG) {
        if (isDebugMode) {
            Log.d(tag, message)
        }
    }
    
    fun i(message: String, tag: String = TAG) {
        if (isDebugMode) {
            Log.i(tag, message)
        }
    }
    
    fun w(message: String, tag: String = TAG) {
        if (isDebugMode) {
            Log.w(tag, message)
        }
    }
    
    fun e(message: String, throwable: Throwable? = null, tag: String = TAG) {
        if (isDebugMode) {
            if (throwable != null) {
                Log.e(tag, message, throwable)
            } else {
                Log.e(tag, message)
            }
        }
    }
    
    fun setDebugMode(enabled: Boolean) {
        isDebugMode = enabled
    }
}

/**
 * 常用工具函数
 */
object Utils {
    
    /**
     * 格式化时间戳
     */
    fun formatTimestamp(timestamp: Long, pattern: String = "yyyy-MM-dd HH:mm"): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    /**
     * 格式化距离
     */
    fun formatDistance(distanceInMeters: Double): String {
        return when {
            distanceInMeters < 1000 -> "${distanceInMeters.toInt()}m"
            distanceInMeters < 10000 -> String.format("%.1fkm", distanceInMeters / 1000)
            else -> "${(distanceInMeters / 1000).toInt()}km"
        }
    }
    
    /**
     * 格式化价格
     */
    fun formatPrice(price: Double): String {
        return when {
            price == 0.0 -> "免费"
            price < 1 -> String.format("%.2f", price)
            price < 100 -> String.format("%.0f", price)
            else -> String.format("%.0f", price)
        }
    }
    
    /**
     * 验证邮箱格式
     */
    fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }
    
    /**
     * 验证手机号格式
     */
    fun isValidPhone(phone: String): Boolean {
        val phonePattern = "^1[3-9]\\d{9}$"
        return phone.matches(phonePattern.toRegex())
    }
    
    /**
     * 生成随机ID
     */
    fun generateRandomId(): String {
        return UUID.randomUUID().toString()
    }
    
    /**
     * 计算两个时间戳之间的天数差
     */
    fun daysBetween(startTime: Long, endTime: Long): Int {
        val diffInMillis = endTime - startTime
        return (diffInMillis / (24 * 60 * 60 * 1000)).toInt()
    }
    
    /**
     * 获取相对时间描述
     */
    fun getRelativeTimeString(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < 60 * 1000 -> "刚刚"
            diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}分钟前"
            diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}小时前"
            diff < 7 * 24 * 60 * 60 * 1000 -> "${diff / (24 * 60 * 60 * 1000)}天前"
            else -> formatTimestamp(timestamp, "MM-dd")
        }
    }
}

