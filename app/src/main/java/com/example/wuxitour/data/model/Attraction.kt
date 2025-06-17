package com.example.wuxitour.data.model

import com.google.gson.annotations.SerializedName
import java.util.*
import com.google.gson.JsonElement
import com.example.wuxitour.data.model.Location

/**
 * 景点数据模型 - 适配 App 内部逻辑和 Firebase 数据
 * @property id 景点唯一标识
 * @property name 景点名称
 * @property address 景点地址
 * @property tags 景点标签
 * @property location 景点位置
 * @property isFavorite 是否收藏
 * @property photos 照片列表
 * @property rating 评分
 * @property cost 费用
 * @property description 景点描述
 * @property category 景点类别
 * @property detailedDescription 景点详细描述
 * @property reviews 景点评论列表
 * @property website 景点官方网站
 */
data class Attraction(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val tags: List<String> = emptyList(),
    val location: Location? = null,
    var isFavorite: Boolean = false,
    val photos: List<Photo> = emptyList(),
    val rating: Double = 0.0,
    val cost: Double = 0.0,
    val description: String = "",
    val category: String = "",
    val detailedDescription: String = "",
    val reviews: List<Review> = emptyList(),
    val website: String = ""
) {
    /**
     * 获取景点位置信息
     * @return 位置信息字符串
     */
    fun getLocationInfo(): String {
        return address
    }

    /**
     * 获取景点基本信息
     * @return 基本信息字符串
     */
    fun getBasicInfo(): String {
        return """
            名称: $name
            地址: $address
            标签: ${tags.joinToString(", ")}
            评分: $rating
            费用: $cost
            描述: $description
            类别: $category
            官网: $website
        """.trimIndent()
    }

    /**
     * 获取经纬度
     */
    fun getLatLng(): Pair<Double, Double>? {
        return location?.let { Pair(it.lat, it.lng) }
    }

    /**
     * 获取第一张图片URL
     */
    fun getFirstImageUrl(): String? {
        return photos.firstOrNull()?.url
    }

    init {
        require(id.isNotBlank()) { "景点ID不能为空" }
        require(name.isNotBlank()) { "景点名称不能为空" }
        require(address.isNotBlank()) { "景点地址不能为空" }
        require(rating in 0.0..5.0) { "评分必须在0到5之间" }
        require(cost >= 0.0) { "费用不能为负数" }
    }
}

/**
 * 景点照片数据模型
 */
data class Photo(
    val url: String = ""
)

/**
 * 景点类别枚举 (保留，但可能需要调整)
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
 * @property userName 评论作者
 * @property comment 评论内容
 * @property rating 评论评分
 * @property date 评论日期
 */
data class Review(
    val userName: String = "",
    val comment: String = "",
    val rating: Double = 5.0,
    val date: String = ""
)