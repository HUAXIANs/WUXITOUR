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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.wuxitour.data.model.Activity
import com.example.wuxitour.data.model.Banner
import com.example.wuxitour.data.model.Category
import com.example.wuxitour.data.model.WeatherInfo
import com.example.wuxitour.ui.screens.attractions.AttractionCard
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onAttractionClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    uiState.homeData?.let { homeData ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    "欢迎来到无锡",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // --- 新增：轮播图模块 ---
            item {
                BannerSection(banners = homeData.banners)
            }

            // --- 新增：分类导航模块 ---
            item {
                CategorySection(categories = homeData.categories)
            }

            if (homeData.hotAttractions.isNotEmpty()) {
                item {
                    Text(
                        "热门景点",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                items(homeData.hotAttractions, key = { it.id }) { attraction ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        AttractionCard(
                            attraction = attraction,
                            onClick = { onAttractionClick(attraction.id) }
                        )
                    }
                }
            }
        }
    }
}

// --- 新增：轮播图UI组件 ---
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BannerSection(banners: List<Banner>) {
    val pagerState = rememberPagerState(pageCount = { banners.size })

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 32.dp),
            modifier = Modifier.height(160.dp)
        ) { page ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                AsyncImage(
                    model = banners[page].imageUrl,
                    contentDescription = banners[page].title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = androidx.compose.ui.graphics.painter.ColorPainter(Color.Gray)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // 指示器
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}

// --- 新增：分类导航UI组件 ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySection(categories: List<Category>) {
    Column {
        Text(
            "快速导航",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categories) { category ->
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
        }
    }
}


// WeatherCard 和 ActivityCard 等其他组件保持不变
@Composable
fun WeatherCard(weather: WeatherInfo) {
    // ...
}

@Composable
fun ActivityCard(activity: Activity) {
    // ...
}