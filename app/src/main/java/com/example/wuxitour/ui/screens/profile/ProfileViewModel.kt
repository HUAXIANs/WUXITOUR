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
    val showLoginDialog: Boolean = false,
    val showRegisterDialog: Boolean = false,
    val error: String? = null
)

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun onLoginClicked() {
        _uiState.update { it.copy(showLoginDialog = true) }
    }

    fun onDismissLoginDialog() {
        _uiState.update { it.copy(showLoginDialog = false) }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1000)
            val mockUser = User(id="user1", username=username, email="$username@example.com", phone="13800000000", avatar=null, nickname="旅行家 $username", points=1250, level=3)
            _uiState.update {
                it.copy(isLoading = false, isLoggedIn = true, user = mockUser, showLoginDialog = false)
            }
            // 登录成功后加载收藏
            loadFavorites()
        }
    }

    fun logout() {
        _uiState.value = ProfileUiState() // 重置为初始未登录状态
    }

    fun loadFavorites() {
        viewModelScope.launch {
            // 通过订阅来实时更新收藏列表
            MockDataRepository.favoriteIdsFlow.collect {
                _uiState.update { currentState ->
                    currentState.copy(favorites = MockDataRepository.getFavoriteAttractions())
                }
            }
        }
    }

    // 其他暂未实现的函数
    fun loadFootprints() {}
    fun clearError() {}
    fun showRegisterDialog() {}
    fun hideRegisterDialog() {}
    fun register(u: String, p: String, e: String, ph: String) {}
}