package com.example.wuxitour.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wuxitour.data.model.Attraction
import com.example.wuxitour.data.model.User
import com.example.wuxitour.data.model.UserFootprint
import com.example.wuxitour.data.repository.AttractionRepository
import com.example.wuxitour.utils.Logger
import com.example.wuxitour.data.common.NetworkResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.wuxitour.data.repository.UserRepository

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

@HiltViewModel
class ProfileViewModel @Inject constructor(
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
                        is NetworkResult.Success -> {
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
                        is NetworkResult.Error -> {
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
                        is NetworkResult.Empty -> {
                            _uiState.update { it.copy(isLoading = false, isLoggedIn = false, user = null, error = "没有用户数据") }
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
                userRepository.login(username, password).collect { result ->
                    when (result) {
                        is NetworkResult.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                        is NetworkResult.Success -> {
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
                        is NetworkResult.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.message
                                )
                            }
                            Logger.e("登录失败: ${result.message}")
                        }
                        is NetworkResult.Empty -> {
                            _uiState.update { it.copy(isLoading = false, error = "登录成功，但无用户数据") }
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
                    when (result) {
                        is NetworkResult.Success -> {
                            _uiState.value = ProfileUiState()
                        }
                        is NetworkResult.Error -> {
                            Logger.e("退出登录失败: ${result.message}")
                            _uiState.update { it.copy(error = result.message) }
                        }
                        is NetworkResult.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                        is NetworkResult.Empty -> {
                            _uiState.value = ProfileUiState(error = "退出登录成功，但无返回数据")
                        }
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
                userRepository.register(email, password, username).collect { result ->
                    when (result) {
                        is NetworkResult.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                        is NetworkResult.Success -> {
                            login(username, password)
                        }
                        is NetworkResult.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.message
                                )
                            }
                            Logger.e("注册失败: ${result.message}")
                        }
                        is NetworkResult.Empty -> {
                            _uiState.update { it.copy(isLoading = false, error = "注册成功，但无用户数据") }
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
                    is NetworkResult.Success -> {
                        (result.data as? Attraction)?.let { attractions.add(it) }
                    }
                    is NetworkResult.Error -> {
                        Logger.e("获取收藏景点详情失败: ${result.message}")
                        _uiState.update { it.copy(error = result.message) }
                    }
                    is NetworkResult.Loading -> {
                        Logger.d("正在加载景点详情: $id")
                    }
                    is NetworkResult.Empty -> {
                        Logger.d("未找到景点详情: $id")
                    }
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
                    is NetworkResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is NetworkResult.Success -> {
                        val favoriteAttractionIds = result.data as? List<String> ?: emptyList()
                        val favoriteAttractions = fetchAttractionsFromIds(favoriteAttractionIds)
                        _uiState.update {
                            it.copy(isLoading = false, favorites = favoriteAttractions, error = null)
                        }
                    }
                    is NetworkResult.Error -> {
                        Logger.e("加载收藏失败: ${result.message}")
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                    is NetworkResult.Empty -> {
                        Logger.e("加载收藏：数据为空 - ${result.message}")
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            }
        }
    }

    fun addFavorite(attractionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            userRepository.addFavoriteAttraction(attractionId).collect {
                when (it) {
                    is NetworkResult.Loading -> _uiState.update { state -> state.copy(isLoading = true) }
                    is NetworkResult.Success -> {
                        if (it.data as? Boolean == true) loadFavorites()
                        _uiState.update { state -> state.copy(isLoading = false, error = null) }
                    }
                    is NetworkResult.Error -> _uiState.update { state -> state.copy(isLoading = false, error = it.message) }
                    is NetworkResult.Empty -> _uiState.update { state -> state.copy(isLoading = false, error = "添加收藏失败：返回数据为空") }
                }
            }
        }
    }

    fun removeFavorite(attractionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            userRepository.removeFavoriteAttraction(attractionId).collect {
                when (it) {
                    is NetworkResult.Loading -> _uiState.update { state -> state.copy(isLoading = true) }
                    is NetworkResult.Success -> {
                        if (it.data as? Boolean == true) loadFavorites()
                        _uiState.update { state -> state.copy(isLoading = false, error = null) }
                    }
                    is NetworkResult.Error -> _uiState.update { state -> state.copy(isLoading = false, error = it.message) }
                    is NetworkResult.Empty -> _uiState.update { state -> state.copy(isLoading = false, error = "移除收藏失败：返回数据为空") }
                }
            }
        }
    }

    fun toggleFavoriteAttraction(attraction: Attraction) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val userId = _uiState.value.user?.id ?: run {
                _uiState.update { it.copy(isLoading = false, error = "用户未登录") }
                return@launch
            }

            val isCurrentlyFavorite = attraction.isFavorite

            val actionFlow = if (isCurrentlyFavorite) {
                userRepository.removeFavoriteAttraction(attraction.id)
            } else {
                userRepository.addFavoriteAttraction(attraction.id)
            }

            actionFlow.collect {
                when (it) {
                    is NetworkResult.Success -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                favorites = if (isCurrentlyFavorite) {
                                    currentState.favorites.filter { fav -> fav.id != attraction.id }
                                } else {
                                    currentState.favorites + attraction.copy(isFavorite = true)
                                },
                                error = null
                            )
                        }
                        loadFavorites()
                    }
                    is NetworkResult.Error -> {
                        Logger.e("切换收藏状态失败: ${it.message}")
                        _uiState.update { currentState -> currentState.copy(isLoading = false, error = it.message) }
                    }
                    is NetworkResult.Loading -> {
                        _uiState.update { currentState -> currentState.copy(isLoading = true) }
                    }
                    is NetworkResult.Empty -> {
                        _uiState.update { currentState -> currentState.copy(isLoading = false, error = "操作完成，但无返回数据") }
                    }
                }
            }
        }
    }

    fun loadFootprints() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            userRepository.getUserFootprints().collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(isLoading = false, footprints = result.data as? List<UserFootprint> ?: emptyList(), error = null)
                        }
                    }
                    is NetworkResult.Error -> {
                        Logger.e("加载足迹失败: ${result.message}")
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                    is NetworkResult.Empty -> {
                        Logger.e("加载足迹：数据为空 - ${result.message}")
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            }
        }
    }

    fun addUserFootprint(attractionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            userRepository.addUserFootprint(attractionId).collect {
                when (it) {
                    is NetworkResult.Loading -> {
                        _uiState.update { currentState -> currentState.copy(isLoading = true) }
                    }
                    is NetworkResult.Success -> {
                        _uiState.update { currentState -> currentState.copy(isLoading = false, error = null) }
                        loadFootprints()
                    }
                    is NetworkResult.Error -> {
                        Logger.e("添加足迹失败: ${it.message}")
                        _uiState.update { currentState -> currentState.copy(isLoading = false, error = it.message) }
                    }
                    is NetworkResult.Empty -> {
                        _uiState.update { currentState -> currentState.copy(isLoading = false, error = it.message) }
                        Logger.d("添加足迹：数据为空 - ${it.message}")
                    }
                }
            }
        }
    }

    fun removeUserFootprint(attractionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            userRepository.removeUserFootprint(attractionId).collect {
                when (it) {
                    is NetworkResult.Loading -> {
                        _uiState.update { currentState -> currentState.copy(isLoading = true) }
                    }
                    is NetworkResult.Success -> {
                        _uiState.update { currentState -> currentState.copy(isLoading = false, error = null) }
                        loadFootprints()
                    }
                    is NetworkResult.Error -> {
                        Logger.e("移除足迹失败: ${it.message}")
                        _uiState.update { currentState -> currentState.copy(isLoading = false, error = it.message) }
                    }
                    is NetworkResult.Empty -> {
                        _uiState.update { currentState -> currentState.copy(isLoading = false, error = it.message) }
                        Logger.d("移除足迹：数据为空 - ${it.message}")
                    }
                }
            }
        }
    }

    fun showLoginDialog(show: Boolean) {
        _uiState.update { it.copy(showLoginDialog = show) }
    }

    fun showRegisterDialog(show: Boolean) {
        _uiState.update { it.copy(showRegisterDialog = show) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}