package com.example.wuxitour.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.wuxitour.data.model.Activity
import com.example.wuxitour.data.model.Banner
import com.example.wuxitour.data.model.Category
import com.example.wuxitour.data.model.WeatherInfo
import com.example.wuxitour.ui.screens.attractions.AttractionCard
import kotlinx.coroutines.delay
import com.example.wuxitour.data.repository.AttractionRepository
import com.example.wuxitour.data.repository.UserRepository
import androidx.compose.runtime.remember

class HomeViewModelFactory(private val attractionRepository: AttractionRepository, private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(attractionRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAttractionClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadHomeData()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            uiState.homeData?.let { homeData ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    // 轮播图
                    if (homeData.banners.isNotEmpty()) {
                        item {
                            BannerSection(banners = homeData.banners)
                        }
                    }

                    // 分类导航
                    if (homeData.categories.isNotEmpty()) {
                        item {
                            CategorySection(categories = homeData.categories)
                        }
                    }

                    // 热门景点 - 使用延迟加载
                    if (homeData.hotAttractions.isNotEmpty()) {
                        item {
                            Text(
                                "热门景点",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                        items(
                            count = homeData.hotAttractions.size,
                            key = { index -> homeData.hotAttractions[index].id ?: index.toString() }
                        ) { index ->
                            val attraction = homeData.hotAttractions[index]
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .animateItemPlacement()
                            ) {
                                AttractionCard(
                                    attraction = attraction,
                                    onClick = { onAttractionClick(attraction.id ?: "") },
                                    onFavoriteClick = { viewModel.onFavoriteClick(attraction) }
                                )
                            }
                        }
                    }

                    // 天气信息 - 使用延迟加载
                    homeData.weather?.let { weather ->
                        if (weather.temperature.isNotBlank()) {
                            item {
                                WeatherCard(weather = weather)
                            }
                        }
                    }

                    // 活动信息 - 使用延迟加载
                    if (homeData.activities.isNotEmpty()) {
                        item {
                            Text(
                                "近期活动",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                        items(
                            count = homeData.activities.size,
                            key = { index -> homeData.activities[index].id }
                        ) { index ->
                            val activity = homeData.activities[index]
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .animateItemPlacement()
                            ) {
                                ActivityCard(activity = activity)
                            }
                        }
                    }
                }
            }
        }

        // 错误提示
        uiState.error?.let { error ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text(error)
            }
        }
    }
}

// 轮播图UI组件 - 优化性能
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BannerSection(banners: List<Banner>) {
    val pagerState = rememberPagerState(pageCount = { banners.size })
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            pagerState.animateScrollToPage(
                page = (pagerState.currentPage + 1) % banners.size
            )
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) { page ->
        val banner = banners[page]
        AsyncImage(
            model = banner.imageUrl,
            contentDescription = banner.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

// 分类导航 - 优化性能
@Composable
fun CategorySection(categories: List<Category>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            count = categories.size,
            key = { index -> categories[index].id }
        ) { index ->
            val category = categories[index]
            CategoryCard(category = category)
        }
    }
}

// 天气卡片
@Composable
fun WeatherCard(weather: WeatherInfo) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("温度: ${weather.temperature}", style = MaterialTheme.typography.bodyLarge)
            Text("状况: ${weather.condition}", style = MaterialTheme.typography.bodyMedium)
            Text("湿度: ${weather.humidity}", style = MaterialTheme.typography.bodySmall)
            Text("风速: ${weather.windSpeed}", style = MaterialTheme.typography.bodySmall)
            Text("空气质量: ${weather.airQuality}", style = MaterialTheme.typography.bodySmall)
            Text("出行建议: ${weather.suggestion}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

// 活动卡片
@Composable
fun ActivityCard(activity: Activity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                activity.title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                activity.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// 导航卡片 - 添加缺失的 CategoryCard
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryCard(category: Category) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { /* TODO: Navigate to category page */ }
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(category.icon, style = MaterialTheme.typography.headlineSmall)
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(category.name, style = MaterialTheme.typography.bodySmall)
    }
}