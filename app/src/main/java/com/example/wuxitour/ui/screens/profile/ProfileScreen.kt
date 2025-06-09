package com.example.wuxitour.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onNavigateToAttraction: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            viewModel.loadFavorites()
            viewModel.loadFootprints()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            if (uiState.isLoggedIn && uiState.user != null) {
                Text("欢迎, ${uiState.user?.nickname ?: ""}")
                Button(onClick = { viewModel.logout() }) { Text("退出登录") }
            } else {
                Button(onClick = { viewModel.onLoginClicked() }) { Text("登录/注册") }
            }
        }

        if (uiState.isLoggedIn) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("我的收藏", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(Modifier.height(8.dp))
                        if (uiState.favorites.isEmpty()) {
                            Text("暂无收藏")
                        } else {
                            // --- 这里是关键修正 ---
                            // 用我们共用的AttractionCard来展示收藏列表
                            uiState.favorites.forEach { attraction ->
                                com.example.wuxitour.ui.screens.attractions.AttractionCard(
                                    attraction = attraction,
                                    onClick = { onNavigateToAttraction(attraction.id) }
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (uiState.showLoginDialog) {
        // ... 登录对话框的逻辑 ...
    }
}