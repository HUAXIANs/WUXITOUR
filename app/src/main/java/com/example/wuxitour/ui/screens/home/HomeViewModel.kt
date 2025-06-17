package com.example.wuxitour.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wuxitour.data.model.Attraction
import com.example.wuxitour.data.model.HomeData
import com.example.wuxitour.data.repository.AttractionRepository
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
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
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
                attractionRepository.getAttractions("景点").collect { result ->
                    when (result) {
                        is NetworkResult.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                        is NetworkResult.Success -> {
                            val homeData = HomeData(
                                banners = emptyList(), // 暂时设置为空，待实现真实API
                                categories = emptyList(), // 暂时设置为空，待实现真实API
                                hotAttractions = result.data,
                                weather = com.example.wuxitour.data.model.WeatherInfo(
                                    temperature = "25",
                                    condition = "晴",
                                    humidity = "60",
                                    windSpeed = "东北风",
                                    airQuality = "优",
                                    suggestion = "天气晴朗，适合出行",
                                    updateTime = "2024-03-21 12:00"
                                ), // 使用默认值
                                activities = emptyList() // 暂时设置为空，待实现真实API
                            )
                            _uiState.update { it.copy(isLoading = false, homeData = homeData, error = null) }
                            retryCount = 0 // 重置重试计数
                        }
                        is NetworkResult.Empty -> {
                            _uiState.update { it.copy(isLoading = false, error = result.message) }
                            Logger.i("加载热门景点：数据为空 - ${result.message}")
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
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val newFavoriteStatus = !attraction.isFavorite
                val attractionId = attraction.id ?: return@launch
                val actionFlow = if (newFavoriteStatus) {
                    userRepository.addFavoriteAttraction(attractionId)
                } else {
                    userRepository.removeFavoriteAttraction(attractionId)
                }

                actionFlow.collect {
                    when (it) {
                        is NetworkResult.Success -> {
                            _uiState.update { currentState ->
                                val updatedHotAttractions = currentState.homeData?.hotAttractions?.map {
                                    if (it.id == attractionId) it.copy(isFavorite = newFavoriteStatus) else it
                                } ?: emptyList()
                                currentState.copy(
                                    isLoading = false,
                                    error = null,
                                    homeData = currentState.homeData?.copy(
                                        hotAttractions = updatedHotAttractions
                                    )
                                )
                            }
                            Logger.i("收藏操作成功: ${if (newFavoriteStatus) "已收藏" else "已取消收藏"}")
                        }
                        is NetworkResult.Error -> {
                            Logger.e("收藏操作失败: ${it.message}")
                            _uiState.update {
                                currentState -> currentState.copy(
                                    isLoading = false,
                                    error = it.message
                                )
                            }
                        }
                        is NetworkResult.Loading -> {
                            _uiState.update { currentState -> currentState.copy(isLoading = true) }
                        }
                        is NetworkResult.Empty -> {
                            _uiState.update { currentState -> currentState.copy(isLoading = false, error = "收藏操作完成，但无返回数据") }
                        }
                    }
                }
            } catch (e: Exception) {
                Logger.e("收藏操作失败", e)
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "收藏操作失败") }
            }
        }
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val homeData: HomeData? = null,
    val error: String? = null
)