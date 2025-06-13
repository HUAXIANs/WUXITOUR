package com.example.wuxitour.ui.screens.trip

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wuxitour.data.model.Trip
import com.example.wuxitour.utils.Utils
import com.example.wuxitour.data.repository.AttractionRepository
import com.example.wuxitour.data.repository.TripRepository
import com.example.wuxitour.data.repository.UserRepository
import androidx.compose.runtime.remember

class TripViewModelFactory(private val tripRepository: TripRepository, private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TripViewModel(tripRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripScreen(
    onNavigateToTripDetail: (String) -> Unit
) {
    val attractionRepository = remember { AttractionRepository() }
    val tripRepository = remember { TripRepository(attractionRepository) }
    val userRepository = remember { UserRepository() }
    val viewModel: TripViewModel = viewModel(factory = TripViewModelFactory(tripRepository, userRepository))
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showCreateTripDialog(true) }) {
                Icon(Icons.Default.Add, contentDescription = "创建新行程")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Text("我的行程", style = MaterialTheme.typography.headlineMedium) }

            if (uiState.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (uiState.trips.isEmpty()) {
                item {
                    Text(
                        "暂无行程，点击右下角按钮创建吧！",
                        modifier = Modifier.padding(top = 32.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                items(uiState.trips, key = { it.id }) { trip ->
                    TripCard(
                        trip = trip,
                        onClick = { onNavigateToTripDetail(trip.id) },
                        onDelete = { viewModel.deleteTrip(trip.id) }
                    )
                }
            }
        }
    }

    if (uiState.showCreateDialog) {
        CreateTripDialog(
            onDismiss = { viewModel.showCreateTripDialog(false) },
            onCreate = { name, description -> viewModel.createTrip(name, description) }
        )
    }
}

/**
 * 这是补全后的行程卡片UI
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripCard(
    trip: Trip,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(trip.name, style = MaterialTheme.typography.titleLarge)
                // 为删除按钮增加一个小的点击区域，防止误触
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "删除行程",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (trip.description?.isNotBlank() == true) {
                Text(
                    text = trip.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Divider()

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "包含 ${trip.attractions.size} 个景点",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "日期: ${Utils.formatTimestamp(trip.startDate, "yyyy-MM-dd")}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}


@Composable
fun CreateTripDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, description: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // 对话框的UI保持不变
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.padding(16.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("创建新行程", style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("行程名称") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("行程描述 (可选)") })
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("取消") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        if (name.isNotBlank()) {
                            onCreate(name, description)
                        }
                    }) {
                        Text("创建")
                    }
                }
            }
        }
    }
}