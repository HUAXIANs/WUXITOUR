package com.example.wuxitour.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * 景点数据模型
 * @property id 景点唯一标识
 * @property name 景点名称
 * @property description 景点简介
 * @property detailedDescription 景点详细介绍
 * @property imageUrl 景点图片URL
 * @property address 景点地址
 * @property latitude 纬度
 * @property longitude 经度
 * @property openingHours 开放时间
 * @property phone 联系电话
 * @property website 官方网站
 * @property ticketInfo 门票信息
 * @property price 价格
 * @property rating 评分
 * @property category 景点类别
 * @property tags 标签列表
 * @property facilities 设施列表
 * @property reviews 评价列表
 * @property isHot 是否热门
 * @property isFavorite 是否收藏
 */
data class Attraction(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("detailedDescription")
    val detailedDescription: String,
    
    @SerializedName("imageUrl")
    val imageUrl: String,
    
    @SerializedName("address")
    val address: String,
    
    @SerializedName("latitude")
    val latitude: Double,
    
    @SerializedName("longitude")
    val longitude: Double,
    
    @SerializedName("openingHours")
    val openingHours: String,
    
    @SerializedName("phone")
    val phone: String,
    
    @SerializedName("website")
    val website: String,
    
    @SerializedName("ticketInfo")
    val ticketInfo: String,
    
    @SerializedName("price")
    val price: String,
    
    @SerializedName("rating")
    val rating: Float,
    
    @SerializedName("category")
    val category: AttractionCategory,
    
    @SerializedName("tags")
    val tags: List<String>,
    
    @SerializedName("facilities")
    val facilities: List<String>,
    
    @SerializedName("reviews")
    val reviews: MutableList<Review>,
    
    @SerializedName("isHot")
    val isHot: Boolean,

    val isFavorite: Boolean = false
) {
    init {
        require(id.isNotBlank()) { "景点ID不能为空" }
        require(name.isNotBlank()) { "景点名称不能为空" }
        require(description.isNotBlank()) { "景点简介不能为空" }
        require(imageUrl.isNotBlank()) { "景点图片URL不能为空" }
        require(address.isNotBlank()) { "景点地址不能为空" }
        require(latitude in -90.0..90.0) { "纬度必须在-90到90之间" }
        require(longitude in -180.0..180.0) { "经度必须在-180到180之间" }
        require(rating in 0f..5f) { "评分必须在0到5之间" }
    }

    /**
     * 获取景点位置信息
     * @return 位置信息字符串
     */
    fun getLocationInfo(): String {
        return "经度: $longitude, 纬度: $latitude"
    }

    /**
     * 获取景点基本信息
     * @return 基本信息字符串
     */
    fun getBasicInfo(): String {
        return """
            名称: $name
            地址: $address
            开放时间: $openingHours
            门票: $price
            评分: $rating
        """.trimIndent()
    }

    /**
     * 添加评价
     * @param review 评价对象
     */
    fun addReview(review: Review) {
        reviews.add(0, review) // 新评价放在最前面
    }

    /**
     * 获取平均评分
     * @return 平均评分
     */
    fun getAverageRating(): Float {
        return if (reviews.isEmpty()) {
            rating
        } else {
            reviews.map { it.rating }.average().toFloat()
        }
    }

    /**
     * 检查是否开放
     * @return 是否开放
     */
    fun isOpen(): Boolean {
        val currentTime = Calendar.getInstance().time
        val currentHour = currentTime.hours
        val currentMinute = currentTime.minutes
        
        return try {
            val (openHour, openMinute) = openingHours.split("-")[0].split(":").map { it.toInt() }
            val (closeHour, closeMinute) = openingHours.split("-")[1].split(":").map { it.toInt() }
            
            val currentTimeInMinutes = currentHour * 60 + currentMinute
            val openTimeInMinutes = openHour * 60 + openMinute
            val closeTimeInMinutes = closeHour * 60 + closeMinute
            
            currentTimeInMinutes in openTimeInMinutes..closeTimeInMinutes
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * 景点类别枚举
 */
enum class AttractionCategory(val displayName: String) {
    @SerializedName("scenic_spot")
    SCENIC_SPOT("风景名胜"),
    
    @SerializedName("temple")
    TEMPLE("寺庙古迹"),
    
    @SerializedName("museum")
    MUSEUM("博物馆"),
    
    @SerializedName("park")
    PARK("公园"),
    
    @SerializedName("garden")
    GARDEN("古典园林"),
    
    @SerializedName("town")
    TOWN("古镇水乡")
}

/**
 * 评价数据模型
 * @property id 评价ID
 * @property userName 用户名
 * @property userAvatar 用户头像
 * @property rating 评分
 * @property comment 评论内容
 * @property date 评价日期
 * @property images 评价图片列表
 */
data class Review(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("userName")
    val userName: String,
    
    @SerializedName("userAvatar")
    val userAvatar: String,
    
    @SerializedName("rating")
    val rating: Float,
    
    @SerializedName("comment")
    val comment: String,
    
    @SerializedName("date")
    val date: String,
    
    @SerializedName("images")
    val images: List<String>? = emptyList()
) {
    init {
        require(id.isNotBlank()) { "评价ID不能为空" }
        require(userName.isNotBlank()) { "用户名不能为空" }
        require(rating in 0f..5f) { "评分必须在0到5之间" }
        require(comment.isNotBlank()) { "评论内容不能为空" }
        require(date.isNotBlank()) { "评价日期不能为空" }
    }
}