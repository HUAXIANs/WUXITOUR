package com.example.wuxitour.data.model

import com.google.gson.annotations.SerializedName
// 这是修正后的、完整的User模型，解决了所有参数和字段不匹配的问题
data class User(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("username")
    val username: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("phone")
    val phone: String?,
    
    @SerializedName("avatar")
    val avatarUrl: String?,
    
    @SerializedName("nickname")
    val nickname: String,
    
    @SerializedName("points")
    val points: Int,
    
    @SerializedName("level")
    val level: Int,
    
    @SerializedName("favoriteAttractions")
    val favoriteAttractions: List<String> = emptyList(),
    
    @SerializedName("visitedAttractions")
    val visitedAttractions: List<String> = emptyList(),
    
    @SerializedName("trips")
    val trips: List<String> = emptyList(),
    
    @SerializedName("gender")
    val gender: String? = null,
    
    @SerializedName("birthday")
    val birthday: Long? = null,
    
    @SerializedName("location")
    val location: String? = null,
    
    @SerializedName("bio")
    val bio: String? = null,
    
    @SerializedName("preferences")
    val preferences: List<String> = emptyList(),
    
    @SerializedName("isVip")
    val isVip: Boolean = false,
    
    @SerializedName("createdAt")
    val createdAt: Long = 0L,
    
    @SerializedName("updatedAt")
    val updatedAt: Long = 0L
)