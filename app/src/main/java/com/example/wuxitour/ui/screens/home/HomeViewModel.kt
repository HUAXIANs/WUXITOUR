package com.example.wuxitour.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wuxitour.data.model.Attraction
import com.example.wuxitour.data.model.HomeData
import com.example.wuxitour.data.repository.AttractionRepository
import com.example.wuxitour.data.repository.MockDataRepository
import com.example.wuxitour.data.common.NetworkResult
import com.example.wuxitour.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.wuxitour.data.repository.UserRepository

class HomeViewModel(
    private val attractionRepository: AttractionRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // 加载热门景点
                attractionRepository.getAttractions("热门").collect { result ->
                    when (result) {
                        is NetworkResult.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                        is NetworkResult.Success -> {
                            val homeData = HomeData(
                                banners = emptyList(), // 暂时设置为空，待实现真实API
                                categories = emptyList(), // 暂时设置为空，待实现真实API
                                hotAttractions = result.data,
                                weather = com.example.wuxitour.data.model.WeatherInfo("", "", "", "", "", ""), // 暂时设置为空，待实现真实API
                                activities = emptyList() // 暂时设置为空，待实现真实API
                            )
                            _uiState.update { it.copy(isLoading = false, homeData = homeData, error = null) }
                        }
                        is NetworkResult.Error -> {
                            _uiState.update { it.copy(isLoading = false, error = result.message) }
                            Logger.e("加载热门景点失败: ${result.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "加载首页数据失败") }
                Logger.e("加载首页数据失败", e)
            }
        }
    }

    fun onFavoriteClick(attraction: Attraction) {
        viewModelScope.launch {
            val userId = userRepository.getCurrentUser().firstOrNull()?.let { result ->
                if (result is NetworkResult.Success) result.data.id else null
            }
            if (userId == null) {
                // 用户未登录，显示错误信息
                _uiState.update { it.copy(error = "用户未登录，无法收藏") }
                return@launch
            }

            val newFavoriteStatus = !attraction.isFavorite
            val actionFlow = if (newFavoriteStatus) {
                userRepository.addFavoriteAttraction(attraction.id)
            } else {
                userRepository.removeFavoriteAttraction(attraction.id)
            }

            actionFlow.collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                homeData = currentState.homeData?.copy(
                                    hotAttractions = currentState.homeData.hotAttractions.map { item ->
                                        if (item.id == attraction.id) item.copy(isFavorite = newFavoriteStatus) else item
                                    }
                                ),
                                error = null
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        Logger.e("切换收藏状态失败: ${result.message}")
                        _uiState.update { currentState -> currentState.copy(error = result.message) }
                    }
                    is NetworkResult.Loading -> { /* Handle loading state if needed */ }
                }
            }
        }
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val homeData: HomeData? = null,
    val error: String? = null
)