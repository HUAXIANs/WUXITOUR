package com.example.wuxitour.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wuxitour.data.model.Attraction
import com.example.wuxitour.data.model.Trip
import com.example.wuxitour.data.repository.MockDataRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AttractionDetailUiState(
    val isLoading: Boolean = true,
    val attraction: Attraction? = null,
    val isFavorited: Boolean = false,
    val userTrips: List<Trip> = emptyList(),
    val showAddToTripDialog: Boolean = false,
    val error: String? = null
)

class AttractionDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AttractionDetailUiState())
    val uiState: StateFlow<AttractionDetailUiState> = _uiState.asStateFlow()
    // 新增：创建一个私有的、可变的 SharedFlow 用于发送消息
    private val _userMessage = MutableSharedFlow<String>()
    // 新增：创建一个公开的、只读的 SharedFlow 供UI层订阅
    val userMessage = _userMessage.asSharedFlow()

    fun loadAttraction(attractionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 订阅景点列表的变化，这样评论更新后UI可以自动刷新
            MockDataRepository.attractionsFlow.collect { attractions ->
                val attraction = attractions.find { it.id == attractionId }

                if (attraction != null) {
                    // 同时订阅收藏和行程列表的变化
                    combine(
                        MockDataRepository.favoriteIdsFlow,
                        MockDataRepository.tripsFlow
                    ) { favoriteIds, trips ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                attraction = attraction,
                                isFavorited = favoriteIds.contains(attractionId),
                                userTrips = trips
                            )
                        }
                    }.collect()
                } else if (_uiState.value.isLoading) {
                    _uiState.update { it.copy(isLoading = false, error = "未找到该景点") }
                }
            }
        }
    }

    // 新增：提交评论的函数
    fun submitReview(rating: Float, comment: String) {
        uiState.value.attraction?.id?.let { attractionId ->
            MockDataRepository.addReview(attractionId, rating, comment)
        }
    }

    fun toggleFavorite() {
        uiState.value.attraction?.let { MockDataRepository.toggleFavoriteStatus(it.id) }
    }

    fun showAddToTripDialog(show: Boolean) {
        _uiState.update { it.copy(showAddToTripDialog = show) }
    }

    fun addAttractionToTrip(tripId: String) {
        val attractionId = uiState.value.attraction?.id
        if (attractionId != null) {
            // 调用仓库方法将景点添加到行程
            MockDataRepository.addAttractionToTrip(tripId, attractionId)

            // 新增：发送成功提示消息
            viewModelScope.launch {
                // 从仓库获取行程名称，使提示更友好
                val trip = MockDataRepository.getTripById(tripId)
                _userMessage.emit("已成功添加到行程: ${trip?.name ?: ""}")
            }
        }
        // 操作完成后关闭对话框
        showAddToTripDialog(false)
    }
}