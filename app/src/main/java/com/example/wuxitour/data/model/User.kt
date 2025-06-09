package com.example.wuxitour.data.model
// 这是修正后的、完整的User模型，解决了所有参数和字段不匹配的问题
data class User(
    val id: String,
    val username: String,
    val email: String,
    val phone: String?,
    val avatar: String?,
    val nickname: String,
    val points: Int,
    val level: Int,
    val favoriteAttractions: List<String> = emptyList(),
    val visitedAttractions: List<String> = emptyList(),
    val trips: List<String> = emptyList(),
    val gender: String? = null,
    val birthday: Long? = null,
    val location: String? = null,
    val bio: String? = null,
    val preferences: List<String> = emptyList(),
    val isVip: Boolean = false,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

data class UserFootprint(val id: String, val userId: String, val attractionId: String, val visitDate: Long, val photos: List<String>?, val notes: String?)