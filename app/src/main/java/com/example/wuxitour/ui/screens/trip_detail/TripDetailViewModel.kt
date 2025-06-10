package com.example.wuxitour.ui.screens.trip_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wuxitour.data.model.Trip
import com.example.wuxitour.data.model.TripAttraction
import com.example.wuxitour.data.repository.MockDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.*

data class TripDetailUiState(
    val isLoading: Boolean = true,
    val trip: Trip? = null,
    val error: String? = null
)

class TripDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TripDetailUiState())
    val uiState: StateFlow<TripDetailUiState> = _uiState.asStateFlow()

    fun loadTripDetails(tripId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // 订阅行程的变化，从而在景点列表更新后自动刷新UI
            MockDataRepository.tripsFlow.collectLatest { trips ->
                val trip = trips.find { it.id == tripId }
                if (trip != null) {
                    _uiState.update { TripDetailUiState(isLoading = false, trip = trip) }
                } else if (_uiState.value.isLoading) { // 仅在初次加载失败时显示错误
                    _uiState.update { TripDetailUiState(isLoading = false, error = "未找到该行程") }
                }
            }
        }
    }

    // 新增：移除景点的函数
    fun removeAttraction(attractionId: String) {
        val tripId = uiState.value.trip?.id ?: return
        MockDataRepository.removeAttractionFromTrip(tripId, attractionId)
    }

    // 新增：智能排序功能的核心实现
    fun sortAttractionsIntelligently() {
        val trip = uiState.value.trip ?: return
        if (trip.attractions.size < 2) return // 少于2个景点无需排序

        val remainingAttractions = trip.attractions.toMutableList()
        val sortedAttractions = mutableListOf<TripAttraction>()

        // 使用“最近邻”算法进行排序
        var currentAttraction = remainingAttractions.removeFirst()
        sortedAttractions.add(currentAttraction)

        while (remainingAttractions.isNotEmpty()) {
            var nearestAttraction: TripAttraction? = null
            var minDistance = Double.MAX_VALUE

            for (nextAttraction in remainingAttractions) {
                val distance = calculateDistance(
                    currentAttraction.attraction.latitude, currentAttraction.attraction.longitude,
                    nextAttraction.attraction.latitude, nextAttraction.attraction.longitude
                )
                if (distance < minDistance) {
                    minDistance = distance
                    nearestAttraction = nextAttraction
                }
            }

            nearestAttraction?.let {
                sortedAttractions.add(it)
                remainingAttractions.remove(it)
                currentAttraction = it
            } ?: break // 如果找不到下一个，则退出循环
        }

        MockDataRepository.reorderAttractionsInTrip(trip.id, sortedAttractions)
    }

    // 新增：计算两个经纬度坐标之间距离的辅助函数（单位：千米）
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371 // 地球半径
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * asin(sqrt(a))
        return r * c
    }
}