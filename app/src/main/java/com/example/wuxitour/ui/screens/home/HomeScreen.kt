package com.example.wuxitour.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wuxitour.data.model.Activity
import com.example.wuxitour.data.model.WeatherInfo
import com.example.wuxitour.ui.screens.attractions.AttractionCard

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onAttractionClick: (String) -> Unit // 添加导航回调
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    uiState.homeData?.let { homeData ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { Text("欢迎来到无锡", style = MaterialTheme.typography.headlineMedium) }
            homeData.weather?.let {
                item { WeatherCard(it) }
            }
            if (homeData.hotAttractions.isNotEmpty()) {
                item { Text("热门景点", style = MaterialTheme.typography.titleLarge) }
                items(homeData.hotAttractions, key = { it.id }) { attraction ->
                    // --- 这里是关键修正 ---
                    // 为 AttractionCard 传入了 onClick 事件
                    AttractionCard(
                        attraction = attraction,
                        onClick = { onAttractionClick(attraction.id) }
                    )
                }
            }
            if (homeData.activities.isNotEmpty()) {
                item { Text("精彩活动", style = MaterialTheme.typography.titleLarge) }
                items(homeData.activities, key = { it.id }) { activity ->
                    ActivityCard(activity = activity)
                }
            }
        }
    }
}

// WeatherCard 和 ActivityCard 保持不变
@Composable
fun WeatherCard(weather: WeatherInfo) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(weather.temperature, style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(weather.condition, style = MaterialTheme.typography.titleMedium)
                Text("湿度: ${weather.humidity}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun ActivityCard(activity: Activity) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(activity.title, style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text(if (activity.price > 0.0) "¥${activity.price}" else "免费")
                Text(activity.status.name)
            }
        }
    }
}