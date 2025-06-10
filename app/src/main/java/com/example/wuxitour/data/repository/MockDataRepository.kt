package com.example.wuxitour.data.repository

import com.example.wuxitour.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

object MockDataRepository {

    // --- 景点基础数据 ---
    private val _mockAttractions = MutableStateFlow(listOf(
        Attraction(id = "1", name = "太湖", description = "中国五大淡水湖之一", detailedDescription = "太湖是中国五大淡水湖之一...", imageUrl = "https://th.bing.com/th/id/R.cdfbcdb1b53b4771bc2f7e0c74b4f635?rik=Va7WBdIUpGsJ0A&riu=http%3a%2f%2fen.ytz.com.cn%2fstatic%2fupload%2f2021%2f09%2f04%2f202109042232.jpg&ehk=Uy%2batA5eKH6OPlyiNefEKzJOQ%2bKqiW0dXbsNtpEqCcY%3d&risl=&pid=ImgRaw&r=0", rating = 4.5f, price = "免费", category = AttractionCategory.SCENIC_SPOT, tags = listOf("自然", "湖泊"), address = "无锡市滨湖区", latitude = 31.23, longitude = 120.27, openingHours = "全天", phone = "123", website = "web", ticketInfo = "免费", facilities = emptyList(), reviews = mutableListOf(Review("r1", "旅行家小明", "avatar_url", 4.0f, "风景很棒，就是周末人有点多。", "2025-06-08", null)), isHot = true),
        Attraction(id = "2", name = "鼋头渚", description = "太湖第一名胜", detailedDescription = "鼋头渚是太湖北岸的一个半岛...", imageUrl = "https://th.bing.com/th/id/R.682eed289311c603811cc557931c3800?rik=DK2a3SwSHaflMw&riu=http%3a%2f%2fwww.ytz.com.cn%2fstatic%2fupload%2f2021%2f09%2f04%2f202109041460.jpg&ehk=0ce%2fbCvww6LqftDrMCyEEC7GwQvEaZkqctLgcYrg3qw%3d&risl=&pid=ImgRaw&r=0", rating = 4.7f, price = "105元", category = AttractionCategory.SCENIC_SPOT, tags = listOf("樱花", "名胜"), address = "无锡市滨湖区", latitude = 31.26, longitude = 120.24, openingHours = "08:00-17:00", phone = "456", website = "web", ticketInfo = "105元", facilities = emptyList(), reviews = mutableListOf(), isHot = true),
        Attraction(id = "3", name = "灵山大佛", description = "世界著名的青铜立像", detailedDescription = "灵山大佛坐落于无锡马山...", imageUrl = "https://th.bing.com/th/id/R.94a3722050b8e82f978e6747bc4bf7d2?rik=VbeqjgFYYcOBbg&riu=http%3a%2f%2fimg.pconline.com.cn%2fimages%2fupload%2fupc%2ftx%2fphotoblog%2f1110%2f29%2fc10%2f9442225_9442225_1319884128781.jpg&ehk=xuV8jdd6McpwRFd8vL0KfvVpnpkMrexM2HdYtKKXE%2fM%3d&risl=&pid=ImgRaw&r=0", rating = 4.6f, price = "210元", category = AttractionCategory.TEMPLE, tags = listOf("佛教", "大佛"), address = "无锡市马山", latitude = 31.11, longitude = 120.08, openingHours = "07:30-17:30", phone = "789", website = "web", ticketInfo = "210元", facilities = emptyList(), reviews = mutableListOf(), isHot = false),
        Attraction(id="4", name="无锡博物院", description="了解无锡历史文化的重要场所", detailedDescription="无锡博物院是一座集收藏、研究、展示于一体的综合性博物馆...", imageUrl="https://th.bing.com/th/id/R.d6eb409eaa731df7488551d01ce968de?rik=gEyI9ox3Qj2F3w&riu=http%3a%2f%2fpic1.k1u.com%2fk1u%2fmb%2fd%2ffile%2f20181206%2fc4eddfc23f155c57dc62c502f0042312_836_10000.jpg&ehk=k4yev09scvuL%2b2tLBnqOjf0PSCoSGxR0RoTTzMQ7REE%3d&risl=&pid=ImgRaw&r=0", rating=4.5f, price="免费", category=AttractionCategory.MUSEUM, tags=listOf("历史", "文化"), address="无锡市梁溪区钟书路100号", latitude=31.573, longitude=120.317, openingHours="09:00-17:00 (周一闭馆)", phone="0510-85727500", website="http://www.wxmuseum.com/", ticketInfo="免费，需预约", facilities=listOf("储物柜", "讲解服务"), isHot=false, reviews=emptyList()),
        Attraction(id="5", name="寄畅园", description="江南四大名园之一", detailedDescription="寄畅园位于锡惠公园内，是一座始建于明代的古典园林建筑...", imageUrl="https://bkimg.cdn.bcebos.com/pic/a50f4bfbfbedab64034f90820464b8c379310a55b06c?x-bce-process=image/format,f_auto/watermark,image_d2F0ZXIvYmFpa2UyNzI,g_7,xp_5,yp_5,P_20/resize,m_lfit,limit_1,h_1080", rating=4.7f, price="70元", category=AttractionCategory.GARDEN, tags=listOf("古典园林", "历史"), address="无锡市梁溪区惠山直街2号", latitude=31.587, longitude=120.278, openingHours="08:00-17:00", phone="0510-85707117", website="http://www.xihui.com/", ticketInfo="包含在锡惠公园联票内", facilities=listOf("茶室"), isHot=false, reviews=emptyList())
    ))
    val attractionsFlow = _mockAttractions.asStateFlow()

    // --- 收藏功能相关 ---
    private val favoriteAttractionIds = MutableStateFlow(mutableSetOf<String>())
    val favoriteIdsFlow = favoriteAttractionIds.asStateFlow()

    fun toggleFavoriteStatus(attractionId: String) {
        val currentFavorites = favoriteAttractionIds.value.toMutableSet()
        if (currentFavorites.contains(attractionId)) {
            currentFavorites.remove(attractionId)
        } else {
            currentFavorites.add(attractionId)
        }
        favoriteAttractionIds.value = currentFavorites
    }

    fun getFavoriteAttractions(): List<Attraction> {
        return attractionsFlow.value.filter { favoriteAttractionIds.value.contains(it.id) }
    }

    // --- 行程功能相关 ---
    private val trips = MutableStateFlow<MutableList<Trip>>(mutableListOf())
    val tripsFlow = trips.asStateFlow()

    fun createNewTrip(name: String, description: String) {
        val newTrip = Trip(
            id = UUID.randomUUID().toString(), name = name, description = description, totalDays = 1,
            attractions = emptyList(), status = TripStatus.PLANNING,
            startDate = System.currentTimeMillis(), endDate = System.currentTimeMillis() + 86400000L,
            estimatedCost = 0.0
        )
        val currentTrips = trips.value.toMutableList()
        currentTrips.add(newTrip)
        trips.value = currentTrips
    }
    // 新增：删除行程的函数
    fun deleteTrip(tripId: String) {
        val currentTrips = trips.value.toMutableList()
        currentTrips.removeAll { it.id == tripId }
        trips.value = currentTrips
    }
    // 新增：从行程中移除一个景点
    fun removeAttractionFromTrip(tripId: String, attractionId: String) {
        val currentTrips = trips.value.toMutableList()
        val tripIndex = currentTrips.indexOfFirst { it.id == tripId }
        if (tripIndex != -1) {
            val oldTrip = currentTrips[tripIndex]
            val newAttractions = oldTrip.attractions.toMutableList()
            newAttractions.removeAll { it.attraction.id == attractionId }
            currentTrips[tripIndex] = oldTrip.copy(attractions = newAttractions)
            trips.value = currentTrips
        }
    }

    // 新增：为行程中的景点列表重新排序
    fun reorderAttractionsInTrip(tripId: String, sortedAttractions: List<TripAttraction>) {
        val currentTrips = trips.value.toMutableList()
        val tripIndex = currentTrips.indexOfFirst { it.id == tripId }
        if (tripIndex != -1) {
            val oldTrip = currentTrips[tripIndex]
            currentTrips[tripIndex] = oldTrip.copy(attractions = sortedAttractions)
            trips.value = currentTrips
        }
    }
    fun addAttractionToTrip(tripId: String, attractionId: String) {
        val attractionToAdd = getAttractionById(attractionId) ?: return
        val currentTrips = trips.value.toMutableList()
        val tripIndex = currentTrips.indexOfFirst { it.id == tripId }
        if (tripIndex != -1) {
            val oldTrip = currentTrips[tripIndex]
            if (oldTrip.attractions.any { it.attraction.id == attractionId }) return
            val newAttractions = oldTrip.attractions.toMutableList().apply {
                add(TripAttraction(attraction = attractionToAdd, visitDate = System.currentTimeMillis()))
            }
            currentTrips[tripIndex] = oldTrip.copy(attractions = newAttractions)
            trips.value = currentTrips
        }
    }

    fun getTripById(tripId: String): Trip? {
        return trips.value.find { it.id == tripId }
    }

    // --- 评价功能相关 ---
    fun addReview(attractionId: String, rating: Float, comment: String) {
        val newReview = Review(
            id = UUID.randomUUID().toString(),
            userName = "我 (游客)", // 暂用模拟用户名
            userAvatar = "url_placeholder",
            rating = rating,
            comment = comment,
            date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            images = emptyList()
        )

        val currentAttractions = _mockAttractions.value.toMutableList()
        val attractionIndex = currentAttractions.indexOfFirst { it.id == attractionId }
        if (attractionIndex != -1) {
            val oldAttraction = currentAttractions[attractionIndex]
            val updatedReviews = oldAttraction.reviews.toMutableList().apply { add(0, newReview) } // 新评论放最前面
            currentAttractions[attractionIndex] = oldAttraction.copy(reviews = updatedReviews)
            _mockAttractions.value = currentAttractions // 触发StateFlow更新
        }
    }


    // --- 其他通用数据获取函数 ---
    fun getMockAttractions(): List<Attraction> = attractionsFlow.value
    fun getAttractionById(id: String): Attraction? = attractionsFlow.value.find { it.id == id }
    fun getMockHomeData(): HomeData = HomeData(
        weather = WeatherInfo("22°C", "晴", "65%", "3级", "良", "适合出游"),
        banners = listOf(Banner("1", "太湖风光", "https://images.unsplash.com/photo-1596393354350-d79593e8956e?q=80&w=2070")),
        categories = listOf(
            Category("风景名胜", "🏞️"),
            Category("寺庙古迹", "⛩️"),
            Category("古典园林", "🌳"),
            Category("博物馆", "🏛️")
        ),
        hotAttractions = _mockAttractions.value.filter { it.isHot },
        activities = emptyList()
    )
}