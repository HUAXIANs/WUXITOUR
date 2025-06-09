package com.example.wuxitour.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wuxitour.data.model.Attraction
import com.example.wuxitour.data.model.User
import com.example.wuxitour.data.model.UserFootprint
import com.example.wuxitour.data.repository.MockDataRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val user: User? = null,
    val favorites: List<Attraction> = emptyList(),
    val footprints: List<UserFootprint> = emptyList(),
    val showLoginDialog: Boolean = false, // 控制登录对话框
    val showRegisterDialog: Boolean = false, // 控制注册对话框
    val error: String? = null
)

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1000) // 模拟网络延迟
            if (username.isNotBlank() && password.isNotBlank()) {
                val mockUser = User(
                    id = "user1", username = username, email = "$username@example.com", phone = "13800000000",
                    avatar = null, nickname = "旅行家 $username", points = 1250, level = 3
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        user = mockUser,
                        showLoginDialog = false,
                        error = null
                    )
                }
                loadFavorites()
            } else {
                _uiState.update { it.copy(isLoading = false, error = "用户名或密码不能为空") }
            }
        }
    }

    fun logout() {
        _uiState.value = ProfileUiState() // 退出登录后清空所有状态
    }

    fun register(username: String, password: String, email: String, phone: String) {
        // 在模拟场景中，注册成功后直接登录
        login(username, password)
    }

    fun loadFavorites() {
        viewModelScope.launch {
            MockDataRepository.favoriteIdsFlow.collectLatest {
                _uiState.update { currentState ->
                    currentState.copy(favorites = MockDataRepository.getFavoriteAttractions())
                }
            }
        }
    }

    // --- 新增：控制对话框显示的函数 ---
    fun showLoginDialog(show: Boolean) {
        _uiState.update { it.copy(showLoginDialog = show, showRegisterDialog = false) }
    }

    fun showRegisterDialog(show: Boolean) {
        _uiState.update { it.copy(showLoginDialog = false, showRegisterDialog = show) }
    }

    // 其他函数
    fun loadFootprints() { /* 暂不实现 */ }
    fun clearError() { _uiState.update { it.copy(error = null) } }
}