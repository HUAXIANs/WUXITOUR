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
import kotlinx.coroutines.delay

class HomeViewModel(
    private val attractionRepository: AttractionRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var retryCount = 0
    private val maxRetries = 3

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
                            retryCount = 0 // 重置重试计数
                        }
                        is NetworkResult.Error -> {
                            if (retryCount < maxRetries) {
                                retryCount++
                                delay(1000L * retryCount) // 指数退避
                                loadHomeData() // 重试
                            } else {
                                _uiState.update { it.copy(isLoading = false, error = result.message) }
                                Logger.e("加载热门景点失败: ${result.message}")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                if (retryCount < maxRetries) {
                    retryCount++
                    delay(1000L * retryCount) // 指数退避
                    loadHomeData() // 重试
                } else {
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "加载首页数据失败") }
                    Logger.e("加载首页数据失败", e)
                }
            }
        }
    }

    fun onFavoriteClick(attraction: Attraction) {
        viewModelScope.launch {
            try {
                // 这里应该调用收藏相关的API
                // 暂时使用模拟数据
                val updatedAttraction = attraction.copy(isFavorite = !attraction.isFavorite)
                _uiState.update { currentState ->
                    val updatedHotAttractions = currentState.homeData?.hotAttractions?.map {
                        if (it.id == updatedAttraction.id) updatedAttraction else it
                    } ?: emptyList()
                    currentState.copy(
                        homeData = currentState.homeData?.copy(
                            hotAttractions = updatedHotAttractions
                        )
                    )
                }
            } catch (e: Exception) {
                Logger.e("收藏操作失败", e)
                _uiState.update { it.copy(error = "收藏操作失败") }
            }
        }
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val homeData: HomeData? = null,
    val error: String? = null
)