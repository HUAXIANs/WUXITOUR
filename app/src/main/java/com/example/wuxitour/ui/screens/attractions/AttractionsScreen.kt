package com.example.wuxitour.ui.screens.attractions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wuxitour.data.model.Attraction
import com.example.wuxitour.data.model.AttractionCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttractionsScreen(
    viewModel: AttractionsViewModel = viewModel(),
    onAttractionClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val filteredAttractions = remember(uiState) {
        uiState.attractions.filter { attraction ->
            val matchesCategory = uiState.selectedCategory == null || attraction.category == uiState.selectedCategory
            val matchesQuery = uiState.searchQuery.isBlank() || attraction.name.contains(uiState.searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // ... (搜索框和分类筛选UI)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(filteredAttractions, key = { it.id }) { attraction ->
                AttractionCard(attraction, onClick = { onAttractionClick(attraction.id) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttractionCard(attraction: Attraction, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(attraction.name, style = MaterialTheme.typography.titleLarge)
            Text(attraction.description, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
        }
    }
}