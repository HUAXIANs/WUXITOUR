package com.example.wuxitour.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wuxitour.data.model.User
import com.example.wuxitour.data.model.UserFootprint
import com.example.wuxitour.ui.components.LoadingIndicator
import com.example.wuxitour.ui.components.ErrorState
import com.example.wuxitour.ui.theme.spacing
import com.example.wuxitour.ui.screens.attractions.AttractionCard
import com.example.wuxitour.data.repository.UserRepository
import com.example.wuxitour.data.repository.AttractionRepository
import androidx.compose.runtime.remember
import com.example.wuxitour.utils.DateTimeUtils

@Composable
fun ProfileScreen(
    onNavigateToAttraction: (String) -> Unit,
    viewModel: ProfileViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            viewModel.loadFavorites()
            viewModel.loadFootprints()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            item {
                if (uiState.isLoggedIn && uiState.user != null) {
                    UserInfoCard(
                        user = uiState.user!!,
                        onLogout = { viewModel.logout() }
                    )
                } else {
                    GuestUserCard(
                        onLogin = { viewModel.showLoginDialog(true) }
                    )
                }
            }

            if (uiState.isLoggedIn) {
                // 收藏列表
                item {
                    SectionCard(
                        title = "我的收藏",
                        icon = Icons.Default.Favorite
                    ) {
                        if (uiState.favorites.isEmpty()) {
                            EmptyState(message = "暂无收藏")
                        } else {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                            ) {
                                uiState.favorites.forEach { attraction ->
                                    AttractionCard(
                                        attraction = attraction,
                                        onClick = { onNavigateToAttraction(attraction.id ?: "") },
                                        onFavoriteClick = { viewModel.toggleFavoriteAttraction(attraction) }
                                    )
                                }
                            }
                        }
                    }
                }

                // 足迹列表
                item {
                    SectionCard(
                        title = "我的足迹",
                        icon = Icons.Default.History
                    ) {
                        if (uiState.footprints.isEmpty()) {
                            EmptyState(message = "暂无足迹")
                        } else {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                            ) {
                                uiState.footprints.forEach { footprint ->
                                    FootprintItem(footprint = footprint)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 加载指示器
        if (uiState.isLoading) {
            LoadingIndicator()
        }

        // 错误信息
        uiState.error?.let { error ->
            ErrorState(
                description = error,
                onAction = { viewModel.clearError() }
            )
        }
    }

    // 登录对话框
    if (uiState.showLoginDialog) {
        LoginDialog(
            isLoading = uiState.isLoading,
            onDismiss = { viewModel.showLoginDialog(false) },
            onLogin = { username, password -> viewModel.login(username, password) },
            onRegister = { viewModel.showRegisterDialog(true) }
        )
    }

    // 注册对话框
    if (uiState.showRegisterDialog) {
        RegisterDialog(
            isLoading = uiState.isLoading,
            onDismiss = { viewModel.showRegisterDialog(false) },
            onRegister = { username, password, email -> viewModel.register(username, password, email) },
            onLogin = { viewModel.showLoginDialog(true) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserInfoCard(user: User, onLogout: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = "Avatar",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                user.nickname,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                user.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(value = "${user.points}", label = "积分")
                StatItem(value = "Lv.${user.level}", label = "等级")
            }
            OutlinedButton(
                onClick = onLogout,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text("退出登录")
            }
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GuestUserCard(onLogin: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            Text(
                "欢迎来到无锡旅游",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "登录后体验完整功能",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            Button(
                onClick = onLogin,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("登录 / 注册")
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.medium)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            content()
        }
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.large),
        contentAlignment = Alignment.Center
    ) {
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun FootprintItem(footprint: UserFootprint) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = footprint.attractionName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                val visitTime = footprint.visitDate?.toDate()?.time ?: System.currentTimeMillis()
                Text(
                    text = "访问时间: ${DateTimeUtils.formatMillisToDateTime(visitTime)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location Icon",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun LoginDialog(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onLogin: (String, String) -> Unit,
    onRegister: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.medium)
        ) {
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                Text(
                    "登录",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("用户名") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("密码") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { onLogin(username, password) },
                    enabled = !isLoading && username.isNotBlank() && password.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("登录")
                    }
                }
                TextButton(
                    onClick = onRegister,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("没有账号？去注册")
                }
            }
        }
    }
}

@Composable
private fun RegisterDialog(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onRegister: (String, String, String) -> Unit,
    onLogin: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.medium)
        ) {
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                Text(
                    "注册",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("用户名") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("密码") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("邮箱") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { onRegister(username, password, email) },
                    enabled = !isLoading && username.isNotBlank() && password.isNotBlank() && email.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("注册")
                    }
                }
                TextButton(
                    onClick = onLogin,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("已有账号？去登录")
                }
            }
        }
    }
}