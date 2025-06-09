package com.example.wuxitour.data.repository

import com.example.wuxitour.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

object MockDataRepository {

    private val _mockAttractions = MutableStateFlow(listOf(
        Attraction(id = "1", name = "太湖", description = "中国五大淡水湖之一", detailedDescription = "太湖是中国五大淡水湖之一...", imageUrl = "url_placeholder", rating = 4.5f, price = "免费", category = AttractionCategory.SCENIC_SPOT, tags = listOf("自然", "湖泊"), address = "无锡市滨湖区", latitude = 31.23, longitude = 120.27, openingHours = "全天", phone = "123", website = "web", ticketInfo = "免费", facilities = emptyList(), reviews = mutableListOf(Review("r1", "旅行家小明", "avatar_url", 4.0f, "风景很棒，就是周末人有点多。", "2025-06-08", null)), isHot = true),
        Attraction(id = "2", name = "鼋头渚", description = "太湖第一名胜", detailedDescription = "鼋头渚是太湖北岸的一个半岛...", imageUrl = "url_placeholder", rating = 4.7f, price = "105元", category = AttractionCategory.SCENIC_SPOT, tags = listOf("樱花", "名胜"), address = "无锡市滨湖区", latitude = 31.26, longitude = 120.24, openingHours = "08:00-17:00", phone = "456", website = "web", ticketInfo = "105元", facilities = emptyList(), reviews = mutableListOf(), isHot = true),
        Attraction(id = "3", name = "灵山大佛", description = "世界著名的青铜立像", detailedDescription = "灵山大佛坐落于无锡马山...", imageUrl = "url_placeholder", rating = 4.6f, price = "210元", category = AttractionCategory.TEMPLE, tags = listOf("佛教", "大佛"), address = "无锡市马山", latitude = 31.11, longitude = 120.08, openingHours = "07:30-17:30", phone = "789", website = "web", ticketInfo = "210元", facilities = emptyList(), reviews = mutableListOf(), isHot = false),
        // --- 这里是关键修正：为所有新加的景点补全所有参数 ---
        Attraction(id="4", name="无锡博物院", description="了解无锡历史文化的重要场所", detailedDescription="无锡博物院是一座集收藏、研究、展示于一体的综合性博物馆...", imageUrl="", rating=4.5f, price="免费", category=AttractionCategory.MUSEUM, tags=listOf("历史", "文化"), address="无锡市梁溪区钟书路100号", latitude=31.573, longitude=120.317, openingHours="09:00-17:00 (周一闭馆)", phone="0510-85727500", website="http://www.wxmuseum.com/", ticketInfo="免费，需预约", facilities=listOf("储物柜", "讲解服务"), isHot=false, reviews=emptyList()),
        Attraction(id="5", name="寄畅园", description="江南四大名园之一", detailedDescription="寄畅园位于锡惠公园内，是一座始建于明代的古典园林建筑...", imageUrl="", rating=4.7f, price="70元", category=AttractionCategory.GARDEN, tags=listOf("古典园林", "历史"), address="无锡市梁溪区惠山直街2号", latitude=31.587, longitude=120.278, openingHours="08:00-17:00", phone="0510-85707117", website="http://www.xihui.com/", ticketInfo="包含在锡惠公园联票内", facilities=listOf("茶室"), isHot=false, reviews=emptyList())
    ))
    val attractionsFlow = _mockAttractions.asStateFlow()

    private val favoriteAttractionIds = MutableStateFlow(mutableSetOf<String>())
    val favoriteIdsFlow = favoriteAttractionIds.asStateFlow()

    private val trips = MutableStateFlow<MutableList<Trip>>(mutableListOf())
    val tripsFlow = trips.asStateFlow()

    fun addReview(attractionId: String, rating: Float, comment: String) {
        val newReview = Review(id = UUID.randomUUID().toString(), userName = "我 (游客)", userAvatar = "url_placeholder", rating = rating, comment = comment, date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()), images = emptyList())
        val currentAttractions = _mockAttractions.value.toMutableList()
        val attractionIndex = currentAttractions.indexOfFirst { it.id == attractionId }
        if (attractionIndex != -1) {
            val oldAttraction = currentAttractions[attractionIndex]
            val updatedReviews = oldAttraction.reviews.toMutableList().apply { add(0, newReview) }
            currentAttractions[attractionIndex] = oldAttraction.copy(reviews = updatedReviews)
            _mockAttractions.value = currentAttractions
        }
    }

    fun getMockAttractions(): List<Attraction> = attractionsFlow.value
    fun getAttractionById(id: String): Attraction? = attractionsFlow.value.find { it.id == id }
    fun getFavoriteAttractions(): List<Attraction> = attractionsFlow.value.filter { favoriteAttractionIds.value.contains(it.id) }
    fun getMockHomeData(): HomeData = HomeData(weather = WeatherInfo("22°C", "晴", "65%", "3级", "良", "适合出游"), banners = listOf(Banner("1", "太湖风光", "url1")), categories = listOf(Category("风景名胜", "🏞️")), hotAttractions = _mockAttractions.value.filter { it.isHot }, activities = emptyList())
    fun getTripById(tripId: String): Trip? = trips.value.find { it.id == tripId }

    fun toggleFavoriteStatus(attractionId: String) {
        val currentFavorites = favoriteAttractionIds.value.toMutableSet()
        if (currentFavorites.contains(attractionId)) currentFavorites.remove(attractionId) else currentFavorites.add(attractionId)
        favoriteAttractionIds.value = currentFavorites
    }

    fun createNewTrip(name: String, description: String) {
        val newTrip = Trip(id = UUID.randomUUID().toString(), name = name, description = description, totalDays = 1, attractions = emptyList(), status = TripStatus.PLANNING, startDate = System.currentTimeMillis(), endDate = System.currentTimeMillis() + 86400000L, estimatedCost = 0.0)
        val currentTrips = trips.value.toMutableList()
        currentTrips.add(newTrip)
        trips.value = currentTrips
    }

    fun addAttractionToTrip(tripId: String, attractionId: String) {
        val attractionToAdd = getAttractionById(attractionId) ?: return
        val currentTrips = trips.value.toMutableList()
        val tripIndex = currentTrips.indexOfFirst { it.id == tripId }
        if (tripIndex != -1) {
            val oldTrip = currentTrips[tripIndex]
            if (oldTrip.attractions.any { it.attraction.id == attractionId }) return
            val newAttractions = oldTrip.attractions.toMutableList().apply { add(TripAttraction(attraction = attractionToAdd, visitDate = System.currentTimeMillis())) }
            currentTrips[tripIndex] = oldTrip.copy(attractions = newAttractions)
            trips.value = currentTrips
        }
    }
}