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
    val userTrips: List<Trip> = emptyList(), // 新增：用户的所有行程列表
    val showAddToTripDialog: Boolean = false, // 新增：控制对话框显示
    val error: String? = null
)

class AttractionDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AttractionDetailUiState())
    val uiState: StateFlow<AttractionDetailUiState> = _uiState.asStateFlow()

    fun loadAttraction(attractionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val attraction = MockDataRepository.getAttractionById(attractionId)

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
            } else {
                _uiState.update { it.copy(isLoading = false, error = "未找到该景点") }
            }
        }
    }

    fun toggleFavorite() {
        uiState.value.attraction?.let { MockDataRepository.toggleFavoriteStatus(it.id) }
    }

    // --- 新增的函数 ---
    fun showAddToTripDialog(show: Boolean) {
        _uiState.update { it.copy(showAddToTripDialog = show) }
    }

    fun addAttractionToTrip(tripId: String) {
        val attractionId = uiState.value.attraction?.id
        if (attractionId != null) {
            MockDataRepository.addAttractionToTrip(tripId, attractionId)
        }
        showAddToTripDialog(false) // 关闭对话框
    }
}