package com.example.wuxitour.ui.screens.attractions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.filled.Star
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.wuxitour.data.model.Attraction
import com.example.wuxitour.data.model.AttractionCategory
import com.example.wuxitour.data.repository.AttractionRepository
import com.example.wuxitour.data.repository.UserRepository
import androidx.compose.runtime.remember
import com.example.wuxitour.ui.components.LoadingIndicator
import com.example.wuxitour.ui.components.ErrorState
import com.example.wuxitour.ui.components.RatingBar
import com.example.wuxitour.ui.components.PriceTag
import com.example.wuxitour.ui.components.TagChip
import com.example.wuxitour.ui.theme.spacing

class AttractionsViewModelFactory(private val attractionRepository: AttractionRepository, private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AttractionsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AttractionsViewModel(attractionRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun AttractionsScreen(
    onAttractionClick: (Attraction) -> Unit,
    viewModel: AttractionsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredAttractions by viewModel.filteredAttractions.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = MaterialTheme.spacing.medium)
    ) {
        // 搜索栏
        SearchBar(
            query = uiState.searchQuery,
            onQueryChange = viewModel::onSearchQueryChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = MaterialTheme.spacing.medium)
        )

        // 错误信息
        uiState.error?.let { error ->
            ErrorState(
                description = error,
                onAction = { viewModel.refresh() }
            )
        }

        // 加载指示器
        if (uiState.isLoading) {
            LoadingIndicator()
        }

        // 景点列表
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            items(filteredAttractions) { attraction ->
                AttractionCard(
                    attraction = attraction,
                    onClick = { onAttractionClick(attraction) },
                    onFavoriteClick = { viewModel.onFavoriteClick(attraction) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("搜索景点") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "搜索") },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttractionCard(
    attraction: Attraction,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.medium)
        ) {
            // 图片
            AsyncImage(
                model = attraction.photos?.firstOrNull()?.url ?: "",
                contentDescription = attraction.name ?: "景点图片",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            // 标题和收藏按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = attraction.name ?: "名称未知",
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (attraction.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (attraction.isFavorite) "取消收藏" else "收藏",
                        tint = if (attraction.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            // 评分和价格
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RatingBar(rating = attraction.rating.toFloat())
                PriceTag(price = attraction.cost.toFloat())
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            // 描述
            Text(
                text = attraction.description ?: "无简介",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            // 标签（如需恢复请补充Attraction数据类的category、tags字段）
            // Row(
            //     modifier = Modifier.fillMaxWidth(),
            //     horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            // ) {
            //     attraction.category?.let { TagChip(text = it) }
            //     attraction.tags?.forEach { tag: String -> TagChip(text = tag) }
            // }
        }
    }
}