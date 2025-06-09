package com.example.wuxitour.data.repository

import com.example.wuxitour.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object MockDataRepository {

    // --- 模拟的景点数据 (不变) ---
    private val mockAttractions = listOf(
        Attraction(id = "1", name = "太湖", description = "中国五大淡水湖之一", detailedDescription = "...", imageUrl = "url_placeholder", rating = 4.5f, price = "免费", category = AttractionCategory.SCENIC_SPOT, tags = listOf("自然", "湖泊"), address = "无锡市滨湖区", latitude = 31.23, longitude = 120.27, openingHours = "全天", phone = "123", website = "web", ticketInfo = "免费", facilities = emptyList(), reviews = emptyList(), isHot = true),
        Attraction(id = "2", name = "鼋头渚", description = "太湖第一名胜", detailedDescription = "...", imageUrl = "url_placeholder", rating = 4.7f, price = "105元", category = AttractionCategory.SCENIC_SPOT, tags = listOf("樱花", "名胜"), address = "无锡市滨湖区", latitude = 31.26, longitude = 120.24, openingHours = "08:00-17:00", phone = "456", website = "web", ticketInfo = "105元", facilities = emptyList(), reviews = emptyList(), isHot = true),
        Attraction(id = "3", name = "灵山大佛", description = "世界著名的青铜立像", detailedDescription = "...", imageUrl = "url_placeholder", rating = 4.6f, price = "210元", category = AttractionCategory.TEMPLE, tags = listOf("佛教", "大佛"), address = "无锡市马山", latitude = 31.11, longitude = 120.08, openingHours = "07:30-17:30", phone = "789", website = "web", ticketInfo = "210元", facilities = emptyList(), reviews = emptyList(), isHot = false)
    )

    // --- 收藏功能 (不变) ---
    private val favoriteAttractionIds = MutableStateFlow(mutableSetOf<String>())
    val favoriteIdsFlow = favoriteAttractionIds.asStateFlow()
    fun toggleFavoriteStatus(attractionId: String) {
        val currentFavorites = favoriteAttractionIds.value.toMutableSet()
        if (currentFavorites.contains(attractionId)) currentFavorites.remove(attractionId) else currentFavorites.add(attractionId)
        favoriteAttractionIds.value = currentFavorites
    }
    fun getFavoriteAttractions(): List<Attraction> = mockAttractions.filter { favoriteAttractionIds.value.contains(it.id) }

    // --- 新增：用一个可变列表模拟用户的行程 "数据库" ---
    private val trips = MutableStateFlow<MutableList<Trip>>(mutableListOf())

    // --- 新增：提供可观察的行程列表 ---
    val tripsFlow = trips.asStateFlow()

    // --- 新增：创建新行程的函数 ---
    fun createNewTrip(name: String, description: String) {
        val newTrip = Trip(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description,
            totalDays = 1, // 默认为1天
            attractions = emptyList(),
            status = TripStatus.PLANNING,
            startDate = System.currentTimeMillis(),
            endDate = System.currentTimeMillis() + 86400000L,
            estimatedCost = 0.0
        )
        val currentTrips = trips.value
        currentTrips.add(newTrip)
        trips.value = currentTrips
    }

    // --- 新增：添加景点到行程的函数 ---
    fun addAttractionToTrip(tripId: String, attractionId: String) {
        val attractionToAdd = getAttractionById(attractionId) ?: return

        val currentTrips = trips.value
        val tripIndex = currentTrips.indexOfFirst { it.id == tripId }
        if (tripIndex != -1) {
            val oldTrip = currentTrips[tripIndex]
            val newAttractions = oldTrip.attractions.toMutableList().apply {
                add(TripAttraction(attraction = attractionToAdd, visitDate = System.currentTimeMillis()))
            }
            currentTrips[tripIndex] = oldTrip.copy(attractions = newAttractions)
            trips.value = currentTrips
        }
    }

    // --- 以下是原有的函数 ---
    fun getMockAttractions(): List<Attraction> = mockAttractions
    fun getAttractionById(id: String): Attraction? = mockAttractions.find { it.id == id }
    fun getMockHomeData(): HomeData = HomeData(weather = WeatherInfo("22°C", "晴", "65%", "3级", "良", "适合出游"), banners = listOf(Banner("1", "太湖风光", "url1")), categories = listOf(Category("风景名胜", "🏞️")), hotAttractions = mockAttractions.filter { it.isHot }, activities = emptyList())
}