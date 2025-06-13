package com.example.wuxitour.data.repository

import com.example.wuxitour.data.model.*
import com.example.wuxitour.utils.DateTimeUtils
import com.example.wuxitour.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

/**
 * 模拟数据仓库
 */
object MockDataRepository {
    // --- 景点基础数据 ---
    private val _mockAttractions = MutableStateFlow(listOf(
        Attraction(
            id = "1",
            name = "太湖",
            description = "中国五大淡水湖之一",
            detailedDescription = "太湖是中国五大淡水湖之一，位于长江三角洲的南缘，横跨江苏、浙江两省，是中国第三大淡水湖。太湖风景秀丽，四季景色各异，是著名的旅游胜地。",
            imageUrl = "https://th.bing.com/th/id/R.cdfbcdb1b53b4771bc2f7e0c74b4f635?rik=Va7WBdIUpGsJ0A&riu=http%3a%2f%2fen.ytz.com.cn%2fstatic%2fupload%2f2021%2f09%2f04%2f202109042232.jpg&ehk=Uy%2batA5eKH6OPlyiNefEKzJOQ%2bKqiW0dXbsNtpEqCcY%3d&risl=&pid=ImgRaw&r=0",
            rating = 4.5f,
            price = "免费",
            category = AttractionCategory.SCENIC_SPOT,
            tags = listOf("自然", "湖泊", "风景"),
            address = "无锡市滨湖区",
            latitude = 31.23,
            longitude = 120.27,
            openingHours = "全天",
            phone = "0510-12345678",
            website = "http://www.taihu.com",
            ticketInfo = "免费开放",
            facilities = listOf("停车场", "游客中心", "观景台"),
            reviews = mutableListOf(
                Review(
                    id = "r1",
                    userName = "旅行家小明",
                    userAvatar = "avatar_url",
                    rating = 4.0f,
                    comment = "风景很棒，就是周末人有点多。",
                    date = DateTimeUtils.getCurrentDate(),
                    images = listOf("review_image_1.jpg")
                )
            ),
            isHot = true
        ),
        Attraction(
            id = "2",
            name = "鼋头渚",
            description = "太湖第一名胜",
            detailedDescription = "鼋头渚是太湖北岸的一个半岛，因形似鼋头而得名。这里四季景色各异，春季樱花盛开，夏季荷花绽放，秋季红叶满山，冬季雪景如画。",
            imageUrl = "https://th.bing.com/th/id/R.682eed289311c603811cc557931c3800?rik=DK2a3SwSHaflMw&riu=http%3a%2f%2fwww.ytz.com.cn%2fstatic%2fupload%2f2021%2f09%2f04%2f202109041460.jpg&ehk=0ce%2fbCvww6LqftDrMCyEEC7GwQvEaZkqctLgcYrg3qw%3d&risl=&pid=ImgRaw&r=0",
            rating = 4.7f,
            price = "105元",
            category = AttractionCategory.SCENIC_SPOT,
            tags = listOf("樱花", "名胜", "赏花"),
            address = "无锡市滨湖区",
            latitude = 31.26,
            longitude = 120.24,
            openingHours = "08:00-17:00",
            phone = "0510-87654321",
            website = "http://www.ytz.com.cn",
            ticketInfo = "成人票105元，学生票52元",
            facilities = listOf("停车场", "游客中心", "餐厅", "商店"),
            reviews = mutableListOf(),
            isHot = true
        ),
        Attraction(
            id = "3",
            name = "灵山大佛",
            description = "世界著名的青铜立像",
            detailedDescription = "灵山大佛坐落于无锡马山，是世界上最大的青铜立像之一。大佛高88米，气势恢宏，是佛教文化的重要象征。",
            imageUrl = "https://th.bing.com/th/id/R.94a3722050b8e82f978e6747bc4bf7d2?rik=VbeqjgFYYcOBbg&riu=http%3a%2f%2fimg.pconline.com.cn%2fimages%2fupload%2fupc%2ftx%2fphotoblog%2f1110%2f29%2fc10%2f9442225_9442225_1319884128781.jpg&ehk=xuV8jdd6McpwRFd8vL0KfvVpnpkMrexM2HdYtKKXE%2fM%3d&risl=&pid=ImgRaw&r=0",
            rating = 4.6f,
            price = "210元",
            category = AttractionCategory.TEMPLE,
            tags = listOf("佛教", "大佛", "文化"),
            address = "无锡市马山",
            latitude = 31.11,
            longitude = 120.08,
            openingHours = "07:30-17:30",
            phone = "0510-76543210",
            website = "http://www.lingshan.com",
            ticketInfo = "成人票210元，学生票105元",
            facilities = listOf("停车场", "游客中心", "素食餐厅", "纪念品商店"),
            reviews = mutableListOf(),
            isHot = false
        ),
        Attraction(
            id = "4",
            name = "无锡博物院",
            description = "了解无锡历史文化的重要场所",
            detailedDescription = "无锡博物院是一座集收藏、研究、展示于一体的综合性博物馆，馆内收藏了大量反映无锡历史文化的珍贵文物。",
            imageUrl = "https://th.bing.com/th/id/R.d6eb409eaa731df7488551d01ce968de?rik=gEyI9ox3Qj2F3w&riu=http%3a%2f%2fpic1.k1u.com%2fk1u%2fmb%2fd%2ffile%2f20181206%2fc4eddfc23f155c57dc62c502f0042312_836_10000.jpg&ehk=k4yev09scvuL%2b2tLBnqOjf0PSCoSGxR0RoTTzMQ7REE%3d&risl=&pid=ImgRaw&r=0",
            rating = 4.5f,
            price = "免费",
            category = AttractionCategory.MUSEUM,
            tags = listOf("历史", "文化", "文物"),
            address = "无锡市梁溪区钟书路100号",
            latitude = 31.573,
            longitude = 120.317,
            openingHours = "09:00-17:00 (周一闭馆)",
            phone = "0510-85727500",
            website = "http://www.wxmuseum.com",
            ticketInfo = "免费，需预约",
            facilities = listOf("储物柜", "讲解服务", "休息区"),
            reviews = mutableListOf(),
            isHot = false
        ),
        Attraction(
            id = "5",
            name = "寄畅园",
            description = "江南四大名园之一",
            detailedDescription = "寄畅园位于锡惠公园内，是一座始建于明代的古典园林建筑。园内亭台楼阁，假山水池，体现了江南园林的精致与典雅。",
            imageUrl = "https://bkimg.cdn.bcebos.com/pic/a50f4bfbfbedab64034f90820464b8c379310a55b06c?x-bce-process=image/format,f_auto/watermark,image_d2F0ZXIvYmFpa2UyNzI,g_7,xp_5,yp_5,P_20/resize,m_lfit,limit_1,h_1080",
            rating = 4.7f,
            price = "70元",
            category = AttractionCategory.GARDEN,
            tags = listOf("古典园林", "历史", "文化"),
            address = "无锡市梁溪区惠山直街2号",
            latitude = 31.587,
            longitude = 120.278,
            openingHours = "08:00-17:00",
            phone = "0510-85707117",
            website = "http://www.xihui.com",
            ticketInfo = "包含在锡惠公园联票内",
            facilities = listOf("茶室", "休息区", "讲解服务"),
            reviews = mutableListOf(),
            isHot = false
        )
    ))
    val attractionsFlow = _mockAttractions.asStateFlow()

    // --- 收藏功能相关 ---
    private val favoriteAttractionIds = MutableStateFlow(mutableSetOf<String>())
    val favoriteIdsFlow = favoriteAttractionIds.asStateFlow()

    fun toggleFavoriteStatus(attractionId: String): Boolean {
        return try {
            val currentFavorites = favoriteAttractionIds.value.toMutableSet()
            if (currentFavorites.contains(attractionId)) {
                currentFavorites.remove(attractionId)
                Logger.i("取消收藏景点: $attractionId")
            } else {
                currentFavorites.add(attractionId)
                Logger.i("收藏景点: $attractionId")
            }
            favoriteAttractionIds.value = currentFavorites
            true
        } catch (e: Exception) {
            Logger.e("收藏状态切换失败", e)
            false
        }
    }

    fun getFavoriteAttractions(): List<Attraction> {
        return try {
            attractionsFlow.value.filter { favoriteAttractionIds.value.contains(it.id) }
        } catch (e: Exception) {
            Logger.e("获取收藏景点失败", e)
            emptyList()
        }
    }

    // --- 行程功能相关 ---
    private val trips = MutableStateFlow<MutableList<Trip>>(mutableListOf())
    val tripsFlow = trips.asStateFlow()

    fun createNewTrip(name: String, description: String): Trip? {
        return try {
            if (name.isBlank()) {
                Logger.w("创建行程失败：名称为空")
                return null
            }
            
            val newTrip = Trip(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description,
                totalDays = 1,
                attractions = emptyList(),
                status = TripStatus.PLANNING,
                startDate = System.currentTimeMillis(),
                endDate = System.currentTimeMillis() + 86400000L,
                estimatedCost = 0.0
            )
            
            val currentTrips = trips.value.toMutableList()
            currentTrips.add(newTrip)
            trips.value = currentTrips
            Logger.i("创建新行程: ${newTrip.id}")
            newTrip
        } catch (e: Exception) {
            Logger.e("创建行程失败", e)
            null
        }
    }

    fun deleteTrip(tripId: String): Boolean {
        return try {
            val currentTrips = trips.value.toMutableList()
            val removed = currentTrips.removeAll { it.id == tripId }
            if (removed) {
                trips.value = currentTrips
                Logger.i("删除行程: $tripId")
            }
            removed
        } catch (e: Exception) {
            Logger.e("删除行程失败", e)
            false
        }
    }

    fun removeAttractionFromTrip(tripId: String, attractionId: String): Boolean {
        return try {
            val currentTrips = trips.value.toMutableList()
            val tripIndex = currentTrips.indexOfFirst { it.id == tripId }
            if (tripIndex != -1) {
                val oldTrip = currentTrips[tripIndex]
                val newAttractions = oldTrip.attractions.toMutableList()
                val removed = newAttractions.removeAll { it.attraction.id == attractionId }
                if (removed) {
                    currentTrips[tripIndex] = oldTrip.copy(attractions = newAttractions)
                    trips.value = currentTrips
                    Logger.i("从行程中移除景点: $attractionId")
                }
                removed
            } else {
                false
            }
        } catch (e: Exception) {
            Logger.e("移除景点失败", e)
            false
        }
    }

    fun reorderAttractionsInTrip(tripId: String, sortedAttractions: List<TripAttraction>): Boolean {
        return try {
            val currentTrips = trips.value.toMutableList()
            val tripIndex = currentTrips.indexOfFirst { it.id == tripId }
            if (tripIndex != -1) {
                val oldTrip = currentTrips[tripIndex]
                currentTrips[tripIndex] = oldTrip.copy(attractions = sortedAttractions)
                trips.value = currentTrips
                Logger.i("重新排序行程景点: $tripId")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Logger.e("重新排序景点失败", e)
            false
        }
    }

    fun addAttractionToTrip(tripId: String, attractionId: String): Boolean {
        return try {
            val attractionToAdd = getAttractionById(attractionId) ?: return false
            val currentTrips = trips.value.toMutableList()
            val tripIndex = currentTrips.indexOfFirst { it.id == tripId }
            if (tripIndex != -1) {
                val oldTrip = currentTrips[tripIndex]
                if (oldTrip.attractions.any { it.attraction.id == attractionId }) {
                    Logger.w("景点已在行程中: $attractionId")
                    return false
                }
                val newAttractions = oldTrip.attractions.toMutableList().apply {
                    add(TripAttraction(attraction = attractionToAdd, visitDate = System.currentTimeMillis()))
                }
                currentTrips[tripIndex] = oldTrip.copy(attractions = newAttractions)
                trips.value = currentTrips
                Logger.i("添加景点到行程: $attractionId")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Logger.e("添加景点到行程失败", e)
            false
        }
    }

    fun getTripById(tripId: String): Trip? {
        return try {
            trips.value.find { it.id == tripId }
        } catch (e: Exception) {
            Logger.e("获取行程失败", e)
            null
        }
    }

    // --- 评价功能相关 ---
    fun addReview(attractionId: String, rating: Float, comment: String): Boolean {
        return try {
            if (rating < 0 || rating > 5) {
                Logger.w("评分无效: $rating")
                return false
            }
            if (comment.isBlank()) {
                Logger.w("评论内容为空")
                return false
            }

            val newReview = Review(
                id = UUID.randomUUID().toString(),
                userName = "我 (游客)",
                userAvatar = "url_placeholder",
                rating = rating,
                comment = comment,
                date = DateTimeUtils.getCurrentDate(),
                images = emptyList()
            )

            val currentAttractions = _mockAttractions.value.toMutableList()
            val attractionIndex = currentAttractions.indexOfFirst { it.id == attractionId }
            if (attractionIndex != -1) {
                val oldAttraction = currentAttractions[attractionIndex]
                val updatedReviews = oldAttraction.reviews.toMutableList().apply { add(0, newReview) }
                currentAttractions[attractionIndex] = oldAttraction.copy(reviews = updatedReviews)
                _mockAttractions.value = currentAttractions
                Logger.i("添加评价成功: $attractionId")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Logger.e("添加评价失败", e)
            false
        }
    }

    // --- 其他通用数据获取函数 ---
    fun getMockAttractions(): List<Attraction> {
        return try {
            attractionsFlow.value
        } catch (e: Exception) {
            Logger.e("获取景点列表失败", e)
            emptyList()
        }
    }

    fun getAttractionById(id: String): Attraction? {
        return try {
            attractionsFlow.value.find { it.id == id }
        } catch (e: Exception) {
            Logger.e("获取景点详情失败", e)
            null
        }
    }

    fun getMockHomeData(): HomeData {
        return try {
            HomeData(
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
        } catch (e: Exception) {
            Logger.e("获取首页数据失败", e)
            HomeData(
                weather = WeatherInfo("--", "--", "--", "--", "--", "--"),
                banners = emptyList(),
                categories = emptyList(),
                hotAttractions = emptyList(),
                activities = emptyList()
            )
        }
    }

    // --- 内存管理 ---
    fun clearCache() {
        try {
            _mockAttractions.value = emptyList()
            favoriteAttractionIds.value = mutableSetOf()
            trips.value = mutableListOf()
            Logger.i("清理缓存数据")
        } catch (e: Exception) {
            Logger.e("清理缓存失败", e)
        }
    }
}