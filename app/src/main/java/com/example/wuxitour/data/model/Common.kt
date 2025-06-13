package com.example.wuxitour.data.model
// 内容与上次相同，确保统一
data class HomeData(val weather: WeatherInfo?, val banners: List<Banner>, val categories: List<Category>, val hotAttractions: List<Attraction>, val activities: List<Activity>)
data class WeatherInfo(val temperature: String, val condition: String, val humidity: String, val windSpeed: String, val airQuality: String, val suggestion: String)
data class Category(val name: String, val icon: String)
data class Banner(val id: String, val title: String, val imageUrl: String, val link: String? = null)
data class Activity(val id: String, val title: String, val description: String, val location: String, val date: String, val status: ActivityStatus, val price: Double, val currentParticipants: Int, val maxParticipants: Int, val imageUrl: String)
enum class ActivityStatus { ONGOING, UPCOMING, ENDED, CANCELLED }