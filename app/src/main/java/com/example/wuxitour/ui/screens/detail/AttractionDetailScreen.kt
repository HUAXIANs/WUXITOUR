package com.example.wuxitour.ui.screens.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wuxitour.data.model.Attraction
import com.example.wuxitour.data.model.Trip

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

    Scaffold(
        // ... (TopAppBar 保持不变)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (uiState.isLoading) {
                // ...
            } else {
                uiState.attraction?.let { attraction ->
                    Column {
                        AttractionDetailContent(modifier = Modifier.weight(1f), attraction = attraction)
                        // --- 新增：底部操作栏 ---
                        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedButton(onClick = { /* TODO */ }, modifier = Modifier.weight(1f)) { Text("语音导览") }
                            Button(onClick = { viewModel.showAddToTripDialog(true) }, modifier = Modifier.weight(1f)) { Text("加入行程") }
                        }
                    }
                }
            }
        }
    }

    // --- 新增：添加到行程的对话框 ---
    if (uiState.showAddToTripDialog) {
        AddToTripDialog(
            trips = uiState.userTrips,
            onDismiss = { viewModel.showAddToTripDialog(false) },
            onTripSelected = { tripId -> viewModel.addAttractionToTrip(tripId) }
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

@Composable
fun AttractionDetailContent(modifier: Modifier = Modifier, attraction: Attraction) {
    // ... (景点详情内容UI保持不变)
}