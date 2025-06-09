package com.example.wuxitour.ui.screens.attractions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.wuxitour.data.model.Attraction
import com.example.wuxitour.data.model.AttractionCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttractionsScreen(
    viewModel: AttractionsViewModel = viewModel(),
    onAttractionClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    // 直接从ViewModel订阅筛选后的列表
    val filteredAttractions by viewModel.filteredAttractions.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // --- 搜索和分类UI ---
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                label = { Text("搜索景点名称、描述...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "搜索") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = uiState.selectedCategory == null,
                        onClick = { viewModel.onCategorySelected(null) },
                        label = { Text("全部") }
                    )
                }
                items(uiState.categories) { category ->
                    FilterChip(
                        selected = uiState.selectedCategory == category,
                        onClick = { viewModel.onCategorySelected(category) },
                        label = { Text(category.displayName) }
                    )
                }
            }
        }

        Divider()

        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (filteredAttractions.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(top = 50.dp), contentAlignment = Alignment.Center) {
                            Text("没有找到相关景点", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                } else {
                    items(filteredAttractions, key = { it.id }) { attraction ->
                        AttractionCard(attraction, onClick = { onAttractionClick(attraction.id) })
                    }
                }
            }
        }
    }
}

// --- “出彩”的景点卡片 ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttractionCard(attraction: Attraction, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = attraction.imageUrl, // 未来换成真实图片URL
                contentDescription = attraction.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop,
                // 简单的占位符
                placeholder = androidx.compose.ui.graphics.painter.ColorPainter(Color.Gray)
            )

            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Text(
                        text = attraction.name,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )
                    if (attraction.isHot) {
                        Surface(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.errorContainer) {
                            Text("热门", color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                Text(
                    text = attraction.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Divider()

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = "地址", modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(attraction.address, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("⭐ ${attraction.rating}", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(16.dp))
                    Text("💰 ${attraction.price}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    Text(attraction.category.displayName, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}