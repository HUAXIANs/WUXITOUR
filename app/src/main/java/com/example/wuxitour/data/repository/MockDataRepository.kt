//// package com.example.wuxitour.data.repository
////
//// import com.example.wuxitour.data.model.*
//// import com.example.wuxitour.utils.DateTimeUtils
//// import com.example.wuxitour.utils.Logger
//// import kotlinx.coroutines.flow.MutableStateFlow
//// import kotlinx.coroutines.flow.asStateFlow
//// import java.util.*
////
//// /**
////  * 模拟数据仓库
////  */
//// object MockDataRepository {
////     // --- 景点基础数据 ---
////     private val _mockAttractions = MutableStateFlow(listOf(
////         Attraction(
////             id = "1",
////             name = "太湖",
////             description = "中国五大淡水湖之一",
////             detailedDescription = "太湖是中国五大淡水湖之一，位于长江三角洲的南缘，横跨江苏、浙江两省，是中国第三大淡水湖。太湖风景秀丽，四季景色各异，是著名的旅游胜地。",
////             imageUrl = "https://th.bing.com/th/id/R.cdfbcdb1b53b4771bc2f7e0c74b4f635?rik=Va7WBdIUpGsJ0A&riu=http%3a%2f%2fen.ytz.com.cn%2fstatic%2fupload%2f2021%2f09%2f04%2f202109042232.jpg&ehk=Uy%2batA5eKH6OPlyiNefEKzJOQ%2bKqiW0dXbsNtpEqCcY%3d&risl=&pid=ImgRaw&r=0",
////             rating = 4.5f,
////             price = "免费",
////             category = AttractionCategory.SCENIC_SPOT,
////             tags = listOf("自然", "湖泊", "风景"),
////             address = "无锡市滨湖区",
////             latitude = 31.23,
////             longitude = 120.27,
////             openingHours = "全天",
////             phone = "0510-12345678",
////             website = listOf("http://www.taihu.com"),
////             ticketInfo = "免费开放",
////             facilities = listOf("停车场", "游客中心", "观景台"),
////             reviews = mutableListOf(
////                 Review(
////                     id = "r1",
////                     userName = "旅行家小明",
////                     userAvatar = "avatar_url",
////                     rating = 4.0f,
////                     comment = "风景很棒，就是周末人有点多。",
////                     date = DateTimeUtils.getCurrentDate(),
////                     images = listOf("review_image_1.jpg")
////                 )
////             ),
////             isHot = true
////         ),
////         Attraction(
////             id = "2",
////             name = "鼋头渚",
////             description = "太湖第一名胜",
////             detailedDescription = "鼋头渚是太湖北岸的一个半岛，因形似鼋头而得名。这里四季景色各异，春季樱花盛开，夏季荷花绽放，秋季红叶满山，冬季雪景如画。",
////             imageUrl = "https://th.bing.com/th/id/R.682eed289311c603811cc557931c3800?rik=DK2a3SwSHaflMw&riu=http%3a%2f%2fwww.ytz.com.cn%2fstatic%2fupload%2f2021%2f09%2f04%2f202109041460.jpg&ehk=0ce%2fbCvww6LqftDrMCyEEC7GwQvEaZkqctLgcYrg3qw%3d&risl=&pid=ImgRaw&r=0",
////             rating = 4.7f,
////             price = "105元",
////             category = AttractionCategory.SCENIC_SPOT,
////             tags = listOf("樱花", "名胜", "赏花"),
////             address = "无锡市滨湖区",
////             latitude = 31.26,
////             longitude = 120.24,
////             openingHours = "08:00-17:00",
////             phone = "0510-87654321",
////             website = listOf("http://www.ytz.com.cn"),
////             ticketInfo = "成人票105元，学生票52元",
////             facilities = listOf("停车场", "游客中心", "餐厅", "商店"),
////             reviews = mutableListOf(),
////             isHot = true
////         ),
////         Attraction(
////             id = "3",
////             name = "灵山大佛",
////             description = "世界著名的青铜立像",
////             detailedDescription = "灵山大佛坐落于无锡马山，是世界上最大的青铜立像之一。大佛高88米，气势恢宏，是佛教文化的重要象征。",
////             imageUrl = "https://th.bing.com/th/id/R.94a3722050b8e82f978e6747bc4bf7d2?rik=VbeqjgFYYcOBbg&riu=http%3a%2f%2fimg.pconline.com.cn%2fimages%2fupload%2fupc%2ftx%2fphotoblog%2f1110%2f29%2fc10%2f9442225_9442225_1319884128781.jpg&ehk=xuV8jdd6McpwRFd8vL0KfvVpnpkMrexM2HdYtKKXE%2fM%3d&risl=&pid=ImgRaw&r=0",
////             rating = 4.6f,
////             price = "210元",
////             category = AttractionCategory.TEMPLE,
////             tags = listOf("佛教", "大佛", "文化"),
////             address = "无锡市马山",
////             latitude = 31.11,
////             longitude = 120.08,
////             openingHours = "07:30-17:30",
////             phone = "0510-76543210",
////             website = listOf("http://www.lingshan.com"),
////             ticketInfo = "成人票210元，学生票105元",
////             facilities = listOf("停车场", "游客中心", "素食餐厅", "纪念品商店"),
////             reviews = mutableListOf(),
////             isHot = false
////         ),
////         Attraction(
////             id = "4",
////             name = "无锡博物院",
////             description = "了解无锡历史文化的重要场所",
////             detailedDescription = "无锡博物院是一座集收藏、研究、展示于一体的综合性博物馆，馆内收藏了大量反映无锡历史文化的珍贵文物。",
////             imageUrl = "https://th.bing.com/th/id/R.d6eb409eaa731df7488551d01ce968de?rik=gEyI9ox3Qj2F3w&riu=http%3a%2f%2fpic1.k1u.com%2fk1u%2fmb%2fd%2ffile%2f20181206%2fc4eddfc23f155c57dc62c502f0042312_836_10000.jpg&ehk=k4yev09scvuL%2b2tLBnqOjf0PSCoSGxR0RoTTzMQ7REE%3d&risl=&pid=ImgRaw&r=0",
////             rating = 4.5f,
////             price = "免费",
////             category = AttractionCategory.MUSEUM,
////             tags = listOf("历史", "文化", "文物"),
////             address = "无锡市梁溪区钟书路100号",
////             latitude = 31.573,
////             longitude = 120.317,
////             openingHours = "09:00-17:00 (周一闭馆)",
////             phone = "0510-85727500",
////             website = listOf("http://www.wxmuseum.com"),
////             ticketInfo = "免费，需预约",
////             facilities = listOf("储物柜", "讲解服务", "休息区"),
////             reviews = mutableListOf(),
////             isHot = false
////         ),
////         Attraction(
////             id = "5",
////             name = "寄畅园",
////             description = "江南四大名园之一",
////             detailedDescription = "寄畅园位于锡惠公园内，是一座始建于明代的古典园林建筑。园内亭台楼阁，假山水池，体现了江南园林的精致与典雅。",
////             imageUrl = "https://bkimg.cdn.bcebos.com/pic/a50f4bfbfbedab64034f90820464b8c379310a55b06c?x-bce-process=image/format,f_auto/watermark,image_d2F0ZXIvYmFpa2UyNzI,g_7,xp_5,yp_5,P_20/resize,m_lfit,limit_1,h_1080",
////             rating = 4.7f,
////             price = "70元",
////             category = AttractionCategory.GARDEN,
////             tags = listOf("古典园林", "历史", "文化"),
////             address = "无锡市梁溪区惠山直街2号",
////             latitude = 31.587,
////             longitude = 120.278,
////             openingHours = "08:00-17:00",
////             phone = "0510-85707117",
////             website = listOf("http://www.xihui.com"),
////             ticketInfo = "包含在锡惠公园联票内",
////             facilities = listOf("茶室", "休息区", "讲解服务"),
////             reviews = mutableListOf(),
////             isHot = false
////         )
////     ))
////     val attractionsFlow = _mockAttractions.asStateFlow()
////
////     // --- 收藏功能相关 ---
////     private val favoriteAttractionIds = MutableStateFlow(mutableSetOf<String>())
////     val favoriteIdsFlow = favoriteAttractionIds.asStateFlow()
////
////     fun toggleFavoriteStatus(attractionId: String): Boolean {
////         return try {
////             val currentFavorites = favoriteAttractionIds.value.toMutableSet()
////             if (currentFavorites.contains(attractionId)) {
////                 currentFavorites.remove(attractionId)
////                 Logger.i("取消收藏景点: $attractionId")
////             } else {
////                 currentFavorites.add(attractionId)
////                 Logger.i("收藏景点: $attractionId")
////             }
////             favoriteAttractionIds.value = currentFavorites
////             true
////         } catch (e: Exception) {
////             Logger.e("收藏状态切换失败", e)
////             false
////         }
////     }
////
////     fun getFavoriteAttractions(): List<Attraction> {
////         return try {
////             attractionsFlow.value.filter { favoriteAttractionIds.value.contains(it.id) }
////         } catch (e: Exception) {
////             Logger.e("获取收藏景点失败", e)
////             emptyList()
////         }
////     }
////
////     // --- 行程功能相关 ---
////     private val trips = MutableStateFlow<MutableList<Trip>>(mutableListOf())
////     val tripsFlow = trips.asStateFlow()
////
////     fun createNewTrip(name: String, description: String): Trip? {
////         return try {
////             if (name.isBlank()) {
////                 Logger.w("创建行程失败：名称为空")
////                 return null
////             }
////
////             val newTrip = Trip(
////                 id = UUID.randomUUID().toString(),
////                 name = name,
////                 description = description,
////                 totalDays = 1,
////                 attractions = emptyList(),
////                 status = TripStatus.PLANNING,
////                 startDate = System.currentTimeMillis(),
////                 endDate = System.currentTimeMillis() + 86400000L,
////                 estimatedCost = 0.0
////             )
////
////             val currentTrips = trips.value.toMutableList()
////             currentTrips.add(newTrip)
////             trips.value = currentTrips
////             Logger.i("创建新行程: ${newTrip.id}")
////             newTrip
////         } catch (e: Exception) {
////             Logger.e("创建行程失败", e)
////             null
////         }
////     }
////
////     fun deleteTrip(tripId: String): Boolean {
////         return try {
////             val currentTrips = trips.value.toMutableList()
////             val removed = currentTrips.removeAll { it.id == tripId }
////             if (removed) {
////                 trips.value = currentTrips
////                 Logger.i("删除行程: $tripId")
////             }
////             removed
////         } catch (e: Exception) {
////             Logger.e("删除行程失败", e)
////             false
////         }
////     }
////
////     fun removeAttractionFromTrip(tripId: String, attractionId: String): Boolean {
////         return try {
////             val currentTrips = trips.value.toMutableList()
////             val tripIndex = currentTrips.indexOfFirst { it.id == tripId }
////             if (tripIndex != -1) {
////                 val oldTrip = currentTrips[tripIndex]
////                 val newAttractions = oldTrip.attractions.toMutableList()
////                 val removed = newAttractions.removeAll { it.attraction.id == attractionId }
////                 if (removed) {
////                     currentTrips[tripIndex] = oldTrip.copy(attractions = newAttractions)
////                     trips.value = currentTrips
////                     Logger.i("从行程中移除景点: $attractionId")
////                 }
////                 removed
////             } else {
////                 false
////             }
////         } catch (e: Exception) {
////             Logger.e("移除景点失败", e)
////             false
////         }
////     }
////
////     fun reorderAttractionsInTrip(tripId: String, sortedAttractions: List<TripAttraction>): Boolean {
////         return try {
////             val currentTrips = trips.value.toMutableList()
////             val tripIndex = currentTrips.indexOfFirst { it.id == tripId }
////             if (tripIndex != -1) {
////                 val oldTrip = currentTrips[tripIndex]
////                 currentTrips[tripIndex] = oldTrip.copy(attractions = sortedAttractions)
////                 trips.value = currentTrips
////                 Logger.i("重新排序行程景点: $tripId")
////                 true
////             } else {
////                 false
////             }
////         } catch (e: Exception) {
////             Logger.e("重新排序行程景点失败", e)
////             false
////         }
////     }
////     // ... 其他功能相关 ...
////     fun getAttractionDetail(attractionId: String): Attraction? {
////         return _mockAttractions.value.find { it.id == attractionId }
////     }
////
////     fun searchAttractions(query: String): List<Attraction> {
////         val lowerCaseQuery = query.toLowerCase(Locale.getDefault())
////         return _mockAttractions.value.filter {
////             it.name.toLowerCase(Locale.getDefault()).contains(lowerCaseQuery) ||
////                     it.description.toLowerCase(Locale.getDefault()).contains(lowerCaseQuery) ||
////                     it.tags?.any { tag -> tag.toLowerCase(Locale.getDefault()).contains(lowerCaseQuery) } == true ||
////                     it.address.toLowerCase(Locale.getDefault()).contains(lowerCaseQuery)
////         }
////     }
////
////     // --- 用户足迹相关 ---
////     private val _userFootprints = MutableStateFlow<MutableList<UserFootprint>>(mutableListOf())
////     val userFootprintsFlow = _userFootprints.asStateFlow()
////
////     fun addUserFootprint(footprint: UserFootprint): Boolean {
////         return try {
////             val currentFootprints = _userFootprints.value.toMutableList()
////             currentFootprints.add(footprint)
////             _userFootprints.value = currentFootprints
////             Logger.i("添加用户足迹: ${footprint.attractionName}")
////             true
////         } catch (e: Exception) {
////             Logger.e("添加用户足迹失败", e)
////             false
////         }
////     }
////
////     fun removeUserFootprint(footprintId: String): Boolean {
////         return try {
////             val currentFootprints = _userFootprints.value.toMutableList()
////             val removed = currentFootprints.removeAll { it.id == footprintId }
////             if (removed) {
////                 _userFootprints.value = currentFootprints
////                 Logger.i("移除用户足迹: $footprintId")
////             }
////             removed
////         } catch (e: Exception) {
////             Logger.e("移除用户足迹失败", e)
////             false
////         }
////     }
////
////     // --- 导览功能相关 ---
////     private val _mockGuides = MutableStateFlow(listOf(
////         GuideItem(
////             id = "guide1",
////             title = "无锡经典一日游",
////             description = "涵盖太湖、鼋头渚、灵山大佛的经典线路。",
////             imageUrl = "https://th.bing.com/th/id/R.cdfbcdb1b53b4771bc2f7e0c74b4f635?rik=Va7WBdIUpGsJ0A&riu=http%3a%2f%2fen.ytz.com.cn%2fstatic%2fupload%2f2021%2f09%2f04%2f202109042232.jpg&ehk=Uy%2batA5eKH6OPlyiNefEKzJOQ%2bKqiW0dXbsNtpEqCcY%3d&risl=&pid=ImgRaw&r=0",
////             audioUrl = "audio_classic_wuxi.mp3",
////             duration = "1小时30分",
////             language = "中文",
////             category = "经典线路",
////             attractionIds = listOf("1", "2", "3")
////         ),
////         GuideItem(
////             id = "guide2",
////             title = "无锡园林文化之旅",
////             description = "探索寄畅园、蠡园等江南园林之美。",
////             imageUrl = "https://bkimg.cdn.bcebos.com/pic/a50f4bfbfbedab64034f90820464b8c379310a55b06c?x-bce-process=image/format,f_auto/watermark,image_d2F0ZXIvYmFpa2UyNzI,g_7,xp_5,yp_5,P_20/resize,m_lfit,limit_1,h_1080",
////             audioUrl = "audio_garden_tour.mp3",
////             duration = "1小时",
////             language = "中文",
////             category = "文化探索",
////             attractionIds = listOf("5")
////         )
////     ))
//    val guidesFlow = _mockGuides.asStateFlow()
//
//    fun getGuideById(guideId: String): GuideItem? {
//        return _mockGuides.value.find { it.id == guideId }
//    }
//
//    fun getGuidesByCategory(category: String): List<GuideItem> {
//        return _mockGuides.value.filter { it.category == category }
//    }
//
//    fun getGuidesByLanguage(language: String): List<GuideItem> {
//        return _mockGuides.value.filter { it.language == language }
//    }
//
//    fun searchGuides(query: String): List<GuideItem> {
//        val lowerCaseQuery = query.toLowerCase(Locale.getDefault())
//        return _mockGuides.value.filter {
//            it.title.toLowerCase(Locale.getDefault()).contains(lowerCaseQuery) ||
//                    it.description.toLowerCase(Locale.getDefault()).contains(lowerCaseQuery) ||
//                    it.category?.toLowerCase(Locale.getDefault())?.contains(lowerCaseQuery) == true ||
//                    it.language.toLowerCase(Locale.getDefault()).contains(lowerCaseQuery)
//        }
//    }
//
//    // --- 用户个人信息模拟 (用于ProfileScreen) ---
//    private val _mockUser = MutableStateFlow(
//        User(
//            id = "user1",
//            username = "TestUser",
//            email = "test@example.com",
//            avatarUrl = "https://th.bing.com/th/id/R.5e3e2e0e0e0e0e0e0e0e0e0e0e0e0e0e?rik=E3oH4L9D5D7S0Q&riu=http%3a%2f%2fimg.zcool.cn%2fcommunity%2f01f022554279b900000199f36f9e0e.jpg&ehk=8gR7mG0yR5kG3n8M8M8M8M8M8M8M8M8M&risl=&pid=ImgRaw&r=0",
//            bio = "热爱旅行，探索世界。",
//            phone = "1234567890",
//            gender = "男",
//            address = "无锡市",
//            preferences = listOf("文化", "美食", "自然"),
//            favoriteAttractionIds = mutableListOf("1", "3") // 预设一些收藏景点
//        )
//    )
//    val mockUserFlow = _mockUser.asStateFlow()
//
//    fun updateMockUserProfile(updatedUser: User): Boolean {
//        return try {
//            _mockUser.value = updatedUser
//            Logger.i("模拟用户资料更新成功")
//            true
//        } catch (e: Exception) {
//            Logger.e("模拟用户资料更新失败", e)
//            false
//        }
//    }
//}