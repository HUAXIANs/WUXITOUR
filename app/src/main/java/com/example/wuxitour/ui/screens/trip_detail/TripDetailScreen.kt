package com.example.wuxitour.ui.screens.trip_detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wuxitour.ui.screens.attractions.AttractionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    tripId: String,
    viewModel: TripDetailViewModel = viewModel(),
    onBackClick: () -> Unit,
    onNavigateToAttraction: (String) -> Unit
) {
    LaunchedEffect(tripId) {
        viewModel.loadTripDetails(tripId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val trip = uiState.trip

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(trip?.name ?: "行程详情") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (trip != null) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(trip.name, style = MaterialTheme.typography.headlineMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(trip.description ?: "暂无描述", style = MaterialTheme.typography.bodyMedium)
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { /* TODO: 智能排序逻辑 */ }) {
                                Icon(Icons.Default.Sort, contentDescription = "智能排序")
                                Spacer(Modifier.width(8.dp))
                                Text("智能排序")
                            }
                            OutlinedButton(onClick = { /* TODO: 跳转地图页面 */ }) {
                                Icon(Icons.Default.Map, contentDescription = "在地图上查看")
                                Spacer(Modifier.width(8.dp))
                                Text("在地图上查看")
                            }
                        }
                    }

                    item {
                        Text("行程包含的景点", style = MaterialTheme.typography.titleLarge)
                    }

                    if (trip.attractions.isEmpty()) {
                        item { Text("该行程暂无景点，快去添加吧！") }
                    } else {
                        items(trip.attractions, key = { it.attraction.id }) { tripAttraction ->
                            AttractionCard(
                                attraction = tripAttraction.attraction,
                                onClick = { onNavigateToAttraction(tripAttraction.attraction.id) }
                            )
                        }
                    }
                }
            } else {
                Text(uiState.error ?: "加载失败", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}