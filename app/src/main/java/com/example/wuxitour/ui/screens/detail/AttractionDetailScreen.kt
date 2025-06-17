package com.example.wuxitour.ui.screens.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wuxitour.data.model.Attraction
import com.example.wuxitour.data.model.Review
import com.example.wuxitour.data.model.Trip
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.wuxitour.data.repository.AttractionRepository
import com.example.wuxitour.data.repository.TripRepository
import com.example.wuxitour.data.repository.UserRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.wuxitour.ui.components.RatingBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttractionDetailScreen(
    viewModel: AttractionDetailViewModel,
    attractionId: String,
    onBackClick: () -> Unit
) {
    if (attractionId.isBlank()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("景点ID不能为空")
        }
        return
    }

    LaunchedEffect(attractionId) {
        viewModel.loadAttraction()
    }

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        viewModel.userMessage.collect { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(uiState.attraction?.name ?: "详情") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    if (uiState.attraction != null) {
                        IconButton(onClick = { viewModel.toggleFavorite() }) {
                            Icon(
                                imageVector = if (uiState.isFavorited) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "收藏",
                                tint = if (uiState.isFavorited) Color.Red else LocalContentColor.current
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "错误: ${uiState.error}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadAttraction() }) {
                        Text("重试")
                    }
                }
            } else {
                uiState.attraction?.let { attraction ->
                    Column {
                        AttractionDetailContent(
                            modifier = Modifier.weight(1f),
                            attraction = attraction
                        )
                        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedButton(onClick = { /* TODO */ }, modifier = Modifier.weight(1f)) { Text("语音导览") }
                            Button(onClick = { viewModel.showAddToTripDialog(true) }, modifier = Modifier.weight(1f)) { Text("加入行程") }
                        }
                    }
                }
            }
        }
    }

    if (uiState.showAddToTripDialog) {
        AddToTripDialog(
            trips = uiState.userTrips,
            onDismiss = { viewModel.showAddToTripDialog(false) },
            onTripSelected = { tripId -> viewModel.addAttractionToTrip(tripId) }
        )
    }
}

@Composable
fun AttractionDetailContent(
    modifier: Modifier = Modifier,
    attraction: Attraction
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(attraction.name ?: "名称未知", style = MaterialTheme.typography.headlineMedium)
                Text(attraction.description ?: "无简介", style = MaterialTheme.typography.bodyMedium)
                
                if (attraction.tags?.isNotEmpty() == true) {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        attraction.tags.forEach { tag ->
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = tag,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("详细信息", style = MaterialTheme.typography.titleLarge)
                Text(attraction.detailedDescription ?: "无详细介绍", style = MaterialTheme.typography.bodyMedium)
            }
        }

        item {
            ReviewSection(reviews = attraction.reviews ?: emptyList())
        }

        item {
            val websiteUrl = attraction.website
            if (!websiteUrl.isNullOrBlank()) {
                Text(
                    text = "官网: ${websiteUrl}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { /* TODO: 打开官网链接 */ }
                )
            }
        }

        item {
            GoogleMapSection(latitude = attraction.location?.lat ?: 0.0, longitude = attraction.location?.lng ?: 0.0)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun AddToTripDialog(
    trips: List<Trip>,
    onDismiss: () -> Unit,
    onTripSelected: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column {
                Text("选择一个行程", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
                LazyColumn {
                    if (trips.isEmpty()){
                        item { Text("暂无行程，请先去创建", modifier = Modifier.padding(16.dp)) }
                    } else {
                        items(trips, key = { it.id }) { trip ->
                            Text(
                                text = trip.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onTripSelected(trip.id) }
                                    .padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewSection(reviews: List<Review>) {
    Column {
        Text("用户评价", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        if (reviews.isEmpty()) {
            Text("暂无评价", style = MaterialTheme.typography.bodyMedium)
        } else {
            reviews.forEach { review ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(review.userName, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            RatingBar(review.rating.toFloat())
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(review.comment)
                        Text(review.date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun GoogleMapSection(latitude: Double, longitude: Double) {
    Column {
        Text("地图", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        if (latitude != 0.0 && longitude != 0.0) {
            // Placeholder for Google Map. Actual implementation would involve Google Maps SDK.
            Text("地图坐标: Lat $latitude, Lng $longitude", style = MaterialTheme.typography.bodyMedium)
            // You would integrate GoogleMap composable here
        } else {
            Text("无法加载地图，缺少位置信息", style = MaterialTheme.typography.bodyMedium)
        }
    }
}