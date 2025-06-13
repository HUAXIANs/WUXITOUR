package com.example.wuxitour.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wuxitour.data.model.Attraction
import com.example.wuxitour.data.model.User
import com.example.wuxitour.data.model.UserFootprint
import com.example.wuxitour.data.repository.UserRepository
import com.example.wuxitour.data.repository.AttractionRepository
import com.example.wuxitour.utils.Logger
import com.example.wuxitour.data.common.NetworkResult
import com.example.wuxitour.data.model.LoginRequest
import com.example.wuxitour.data.model.RegisterRequest
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val attractionRepository: AttractionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            try {
                userRepository.getCurrentUser().collect { result ->
                    when (result) {
                        is NetworkResult.Success<User> -> {
                            _uiState.update {
                                it.copy(
                                    isLoggedIn = true,
                                    user = result.data,
                                    error = null
                                )
                            }
                            loadFavorites()
                            loadFootprints()
                        }
                        is NetworkResult.Error<User> -> {
                            _uiState.update {
                                it.copy(
                                    isLoggedIn = false,
                                    user = null,
                                    error = result.message
                                )
                            }
                        }
                        is NetworkResult.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                    }
                }
            } catch (e: Exception) {
                Logger.e("检查登录状态失败", e)
                _uiState.update {
                    it.copy(
                        isLoggedIn = false,
                        user = null,
                        error = e.message ?: "检查登录状态失败"
                    )
                }
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val loginRequest = LoginRequest(username, password)
                userRepository.login(loginRequest).collect { result ->
                    when (result) {
                        is NetworkResult.Loading<User> -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                        is NetworkResult.Success<User> -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isLoggedIn = true,
                                    user = result.data,
                                    showLoginDialog = false,
                                    error = null
                                )
                            }
                            loadFavorites()
                            loadFootprints()
                        }
                        is NetworkResult.Error<User> -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.message
                                )
                            }
                            Logger.e("登录失败: ${result.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "登录失败"
                    )
                }
                Logger.e("登录失败", e)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                userRepository.logout().collect { result ->
                    if (result is NetworkResult.Success) {
                        _uiState.value = ProfileUiState()
                    } else if (result is NetworkResult.Error) {
                        Logger.e("退出登录失败: ${result.message}")
                        _uiState.update { it.copy(error = result.message) }
                    }
                }
            } catch (e: Exception) {
                Logger.e("退出登录失败", e)
                _uiState.update {
                    it.copy(error = e.message ?: "退出登录失败")
                }
            }
        }
    }

    fun register(username: String, password: String, email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val registerRequest = RegisterRequest(username, password, email)
                userRepository.register(registerRequest).collect { result ->
                    when (result) {
                        is NetworkResult.Loading<User> -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                        is NetworkResult.Success<User> -> {
                            login(username, password)
                        }
                        is NetworkResult.Error<User> -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.message
                                )
                            }
                            Logger.e("注册失败: ${result.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "注册失败"
                    )
                }
                Logger.e("注册失败", e)
            }
        }
    }

    private suspend fun fetchAttractionsFromIds(ids: List<String>): List<Attraction> {
        val attractions = mutableListOf<Attraction>()
        for (id in ids) {
            attractionRepository.getAttractionDetail(id).firstOrNull()?.let { result ->
                when (result) {
                    is NetworkResult.Success<Attraction> -> {
                        result.data.let { attractions.add(it) }
                    }
                    is NetworkResult.Error<Attraction> -> {
                        Logger.e("获取收藏景点详情失败: ${result.message}")
                        _uiState.update { it.copy(error = result.message) }
                    }
                    is NetworkResult.Loading<Attraction> -> { /* Not handling loading for individual fetches here */ }
                }
            }
        }
        return attractions
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            userRepository.getFavoriteAttractions().collect { result ->
                when (result) {
                    is NetworkResult.Loading<List<String>> -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is NetworkResult.Success<List<String>> -> {
                        val favoriteAttractionIds = result.data
                        val favoriteAttractions = fetchAttractionsFromIds(favoriteAttractionIds)
                        _uiState.update {
                            it.copy(isLoading = false, favorites = favoriteAttractions, error = null)
                        }
                    }
                    is NetworkResult.Error<List<String>> -> {
                        Logger.e("加载收藏失败: ${result.message}")
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            }
        }
    }

    fun showLoginDialog(show: Boolean) {
        _uiState.update { it.copy(showLoginDialog = show, showRegisterDialog = false) }
    }

    fun showRegisterDialog(show: Boolean) {
        _uiState.update { it.copy(showLoginDialog = false, showRegisterDialog = show) }
    }

    fun loadFootprints() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val userId = uiState.value.user?.id ?: run {
                _uiState.update { it.copy(isLoading = false, error = "用户未登录，无法加载足迹") }
                return@launch
            }
            userRepository.getUserFootprints(userId).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is NetworkResult.Success<List<UserFootprint>> -> {
                        _uiState.update {
                            it.copy(isLoading = false, footprints = result.data, error = null)
                        }
                    }
                    is NetworkResult.Error<List<UserFootprint>> -> {
                        Logger.e("加载足迹失败: ${result.message}")
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            }
        }
    }

    fun toggleFavoriteAttraction(attraction: Attraction) {
        viewModelScope.launch {
            val currentFavorites = _uiState.value.favorites
            val isFavorite = currentFavorites.any { it.id == attraction.id }

            if (isFavorite) {
                userRepository.removeFavoriteAttraction(attraction.id).collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            _uiState.update { currentState ->
                                currentState.copy(favorites = currentFavorites.filter { fav -> fav.id != attraction.id })
                            }
                        }
                        is NetworkResult.Error -> {
                            Logger.e("移除收藏失败: ${result.message}")
                            _uiState.update { currentState -> currentState.copy(error = result.message) }
                        }
                        is NetworkResult.Loading -> { /* Handle loading state if needed */ }
                    }
                }
            } else {
                userRepository.addFavoriteAttraction(attraction.id).collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            _uiState.update { currentState ->
                                currentState.copy(favorites = currentState.favorites + attraction)
                            }
                        }
                        is NetworkResult.Error -> {
                            Logger.e("添加收藏失败: ${result.message}")
                            _uiState.update { currentState -> currentState.copy(error = result.message) }
                        }
                        is NetworkResult.Loading -> { /* Handle loading state if needed */ }
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}