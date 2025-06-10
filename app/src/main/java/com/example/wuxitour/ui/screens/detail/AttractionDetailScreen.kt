package com.example.wuxitour.ui.screens.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wuxitour.data.model.Attraction
import com.example.wuxitour.data.model.Review
import com.example.wuxitour.data.model.Trip
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttractionDetailScreen(
    attractionId: String,
    viewModel: AttractionDetailViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    LaunchedEffect(attractionId) {
        viewModel.loadAttraction(attractionId)
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
                            attraction = attraction,
                            onSubmitReview = { rating, comment ->
                                viewModel.submitReview(rating, comment)
                            }
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
    attraction: Attraction,
    onSubmitReview: (rating: Float, comment: String) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Text(attraction.name, style = MaterialTheme.typography.headlineMedium) }
        item { Text(attraction.detailedDescription, style = MaterialTheme.typography.bodyMedium) }

        item {
            ReviewSection(
                reviews = attraction.reviews,
                onSubmitReview = onSubmitReview
            )
        }
    }
}

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

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("为这个景点打分：")
            RatingBar(
                rating = rating,
                onRatingChanged = { rating = it }
            )
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("写下您的评论...") },
                modifier = Modifier.fillMaxWidth().height(100.dp)
            )
            Button(
                onClick = {
                    onSubmit(rating, comment)
                    // 提交后清空
                    rating = 0f
                    comment = ""
                },
                modifier = Modifier.align(Alignment.End),
                enabled = rating > 0f // 必须评分后才能提交
            ) {
                Text("提交")
            }
        }
    }
}

@Composable
fun RatingBar(
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    maxRating: Int = 5
) {
    Row {
        for (i in 1..maxRating) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = if (i <= rating) Color(0xFFFFC107) else Color.Gray,
                modifier = Modifier
                    .size(36.dp)
                    .clickable { onRatingChanged(i.toFloat()) }
            )
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AccountCircle, contentDescription = "avatar", modifier = Modifier.size(40.dp))
            Spacer(Modifier.width(8.dp))
            Column {
                Text(review.userName, fontWeight = FontWeight.Bold)
                Text(review.date, style = MaterialTheme.typography.bodySmall)
            }
        }
        RatingBar(rating = review.rating, onRatingChanged = {})
        Spacer(Modifier.height(4.dp))
        Text(review.comment)
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