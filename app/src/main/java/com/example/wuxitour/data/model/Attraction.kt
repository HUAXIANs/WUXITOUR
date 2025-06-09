package com.example.wuxitour.data.model
// 内容与上次相同，确保统一
data class Attraction(
    val id: String, val name: String, val description: String, val detailedDescription: String,
    val imageUrl: String, val rating: Float, val price: String, val category: AttractionCategory,
    val tags: List<String>, val address: String, val latitude: Double, val longitude: Double,
    val openingHours: String, val phone: String?, val website: String?, val ticketInfo: String?,
    val facilities: List<String>?, val reviews: List<Review> = emptyList(), val isHot: Boolean = false
)
enum class AttractionCategory(val displayName: String) {
    SCENIC_SPOT("风景名胜"), TEMPLE("寺庙古迹"), MUSEUM("博物馆"), PARK("公园")
}
data class Review(
    val id: String, val userName: String, val userAvatar: String, val rating: Float,
    val comment: String, val date: String, val images: List<String>? = emptyList()
)