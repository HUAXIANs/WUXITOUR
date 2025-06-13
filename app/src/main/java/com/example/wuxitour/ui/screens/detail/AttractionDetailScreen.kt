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

class AttractionDetailViewModelFactory(
    private val attractionRepository: AttractionRepository,
    private val tripRepository: TripRepository,
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AttractionDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AttractionDetailViewModel(attractionRepository, tripRepository, userRepository, savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttractionDetailScreen(
    attractionId: String,
    onBackClick: () -> Unit
) {
    val attractionRepository = remember { AttractionRepository() }
    val tripRepository = remember { TripRepository(attractionRepository) }
    val userRepository = remember { UserRepository() }
    val viewModel: AttractionDetailViewModel = viewModel(factory = AttractionDetailViewModelFactory(attractionRepository, tripRepository, userRepository, SavedStateHandle()))

    LaunchedEffect(attractionId) {
        viewModel.loadAttraction()
    }

    val uiState by viewModel.uiState.collectAsState()
// 新增：创建 Snackbar 所需的状态和协程作用域
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    // 新增：使用 LaunchedEffect 来监听来自 ViewModel 的消息
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
                Text(text = "错误: ${uiState.error}", modifier = Modifier.align(Alignment.Center))
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
        // 基本信息
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(attraction.name, style = MaterialTheme.typography.headlineMedium)
                Text(attraction.description, style = MaterialTheme.typography.bodyMedium)
                
                // 标签
                if (attraction.tags.isNotEmpty()) {
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

        // 详细信息
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("详细信息", style = MaterialTheme.typography.titleLarge)
                Text(attraction.detailedDescription, style = MaterialTheme.typography.bodyMedium)
            }
        }

        // 开放信息
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("开放信息", style = MaterialTheme.typography.titleLarge)
                InfoRow("开放时间", attraction.openingHours)
                InfoRow("门票信息", attraction.ticketInfo)
                InfoRow("价格", attraction.price)
                InfoRow("联系电话", attraction.phone)
                InfoRow("官方网站", attraction.website)
            }
        }

        // 设施信息
        if (attraction.facilities.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("设施服务", style = MaterialTheme.typography.titleLarge)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        attraction.facilities.forEach { facility ->
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.tertiaryContainer
                            ) {
                                Text(
                                    text = facility,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
        }

        // 评价部分
        // TODO: Future: Re-implement review functionality if a dedicated ReviewRepository is added
        // item {
        //     ReviewSection(\n        //         reviews = attraction.reviews,\n        //         onSubmitReview = onSubmitReview\n        //     )\n        // }\n    }
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

// TODO: Future: Re-implement review functionality if a dedicated ReviewRepository is added
/*
@Composable
fun ReviewSection(
    reviews: List<Review>,
    onSubmitReview: (rating: Float, comment: String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Divider()
        Text("发表您的看法", style = MaterialTheme.typography.titleLarge)
        ReviewInput(onSubmit = onSubmitReview)

        Spacer(Modifier.height(8.dp))

        Text("全部评价 (${reviews.size})", style = MaterialTheme.typography.titleLarge)
        if (reviews.isEmpty()) {
            Text("暂无评价，快来抢占第一个沙发吧！")
        } else {
            reviews.forEach { review ->
                ReviewItem(review = review)
                Divider(modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewInput(
    onSubmit: (rating: Float, comment: String) -> Unit
) {
    var rating by remember { mutableStateOf(0f) }
    var comment by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("评分:", modifier = Modifier.width(50.dp))
            StarRatingBar(rating = rating) { newRating -> rating = newRating }
        }
        OutlinedTextField(
            value = comment,
            onValueChange = { comment = it },
            label = { Text("您的评论") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        Button(onClick = { onSubmit(rating, comment) }) {
            Text("提交评论")
        }
    }
}

@Composable
fun StarRatingBar(rating: Float, onRatingChanged: (Float) -> Unit) {
    Row {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = if (i <= rating) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier
                    .clickable { onRatingChanged(i.toFloat()) }
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(review.userName, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.width(8.dp))
            StarRatingBar(rating = review.rating, onRatingChanged = { /* Read-only */ })
        }
        Text(review.comment, style = MaterialTheme.typography.bodyMedium)
        Text(review.date, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}
*/

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