package com.example.wuxitour.ui.screens.trip

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wuxitour.data.model.Trip
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripScreen(viewModel: TripViewModel = viewModel()) {
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
            if (uiState.trips.isEmpty()) {
                item { Text("暂无行程，点击右下角按钮创建吧！") }
            } else {
                items(uiState.trips, key = { it.id }) { trip ->
                    TripCard(trip = trip, onDelete = { viewModel.deleteTrip(trip.id) })
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

@Composable
fun TripCard(trip: Trip, onDelete: () -> Unit) {
    // ... TripCard UI 保持不变 ...
}

@Composable
fun CreateTripDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, description: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.padding(16.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("创建新行程", style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("行程名称") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("行程描述 (可选)") })
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("取消") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { onCreate(name, description) }) { Text("创建") }
                }
            }
        }
    }
}