package com.example.wuxitour.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wuxitour.data.model.User
import com.example.wuxitour.ui.screens.attractions.AttractionCard

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

    // --- 关键修正：确保 LazyColumn 存在 ---
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            if (uiState.isLoggedIn && uiState.user != null) {
                UserInfoCard(user = uiState.user!!, onLogout = { viewModel.logout() })
            } else {
                GuestUserCard(onLogin = { viewModel.showLoginDialog(true) })
            }
        }

        if (uiState.isLoggedIn) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("我的收藏", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(8.dp))
                        if (uiState.favorites.isEmpty()) {
                            Text("暂无收藏")
                        } else {
                            uiState.favorites.forEach { attraction ->
                                AttractionCard(
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
        LoginDialog(
            isLoading = uiState.isLoading,
            onDismiss = { viewModel.showLoginDialog(false) },
            onLogin = { username, password -> viewModel.login(username, password) },
            onRegister = { viewModel.showRegisterDialog(true) }
        )
    }

    if (uiState.showRegisterDialog) {
        RegisterDialog(
            isLoading = uiState.isLoading,
            onDismiss = { viewModel.showRegisterDialog(false) },
            onRegister = { username, password, email, phone -> viewModel.register(username, password, email, phone) },
            onLogin = { viewModel.showLoginDialog(true) }
        )
    }
}

// --- 新增：补全缺失的UserInfoCard组件 ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfoCard(user: User, onLogout: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.Person, contentDescription = "Avatar", modifier = Modifier.size(80.dp))
            Text(user.nickname, style = MaterialTheme.typography.titleLarge)
            Text(user.email, style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${user.points}", style = MaterialTheme.typography.titleMedium)
                    Text("积分", style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Lv.${user.level}", style = MaterialTheme.typography.titleMedium)
                    Text("等级", style = MaterialTheme.typography.bodySmall)
                }
            }
            Button(onClick = onLogout) { Text("退出登录") }
        }
    }
}

// --- 新增：补全缺失的GuestUserCard组件 ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestUserCard(onLogin: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("欢迎来到无锡旅游", style = MaterialTheme.typography.titleMedium)
            Text("登录后体验完整功能", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Button(onClick = onLogin) {
                Text("登录 / 注册")
            }
        }
    }
}

// --- 其他对话框组件保持不变 ---
@Composable
fun LoginDialog(isLoading: Boolean, onDismiss: () -> Unit, onLogin: (String, String) -> Unit, onRegister: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("登录", style = MaterialTheme.typography.headlineSmall)
                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("用户名") })
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("密码") }, visualTransformation = PasswordVisualTransformation())
                Button(onClick = { onLogin(username, password) }, enabled = !isLoading, modifier = Modifier.fillMaxWidth()) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp)) else Text("登录")
                }
                TextButton(onClick = onRegister, modifier = Modifier.align(Alignment.End)) { Text("没有账号？去注册") }
            }
        }
    }
}

@Composable
fun RegisterDialog(isLoading: Boolean, onDismiss: () -> Unit, onRegister: (String, String, String, String) -> Unit, onLogin: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("注册", style = MaterialTheme.typography.headlineSmall)
                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("用户名") })
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("密码") }, visualTransformation = PasswordVisualTransformation())
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("邮箱") })
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("手机号 (可选)") })
                Button(onClick = { onRegister(username, password, email, phone) }, enabled = isLoading, modifier = Modifier.fillMaxWidth()) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp)) else Text("注册")
                }
                TextButton(onClick = onLogin, modifier = Modifier.align(Alignment.End)) { Text("已有账号？去登录") }
            }
        }
    }
}