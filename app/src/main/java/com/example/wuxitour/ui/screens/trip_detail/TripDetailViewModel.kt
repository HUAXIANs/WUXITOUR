package com.example.wuxitour.ui.screens.trip_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wuxitour.data.model.Trip
import com.example.wuxitour.data.model.TripAttraction
import com.example.wuxitour.data.repository.TripRepository
import com.example.wuxitour.utils.Logger
import com.example.wuxitour.data.common.NetworkResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.*
import com.example.wuxitour.data.model.Attraction
import com.example.wuxitour.data.repository.UserRepository

data class TripDetailUiState(
    val isLoading: Boolean = true,
    val trip: Trip? = null,
    val error: String? = null
)

class TripDetailViewModel(
    private val repository: TripRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TripDetailUiState())
    val uiState: StateFlow<TripDetailUiState> = _uiState.asStateFlow()

    fun loadTripDetails(tripId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                repository.getTripDetail(tripId).collect { result ->
                    when (result) {
                        is NetworkResult.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                        is NetworkResult.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    trip = result.data,
                                    error = null
                                )
                            }
                        }
                        is NetworkResult.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.message
                                )
                            }
                            Logger.e("加载行程详情失败: ${result.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "加载失败"
                    )
                }
                Logger.e("加载行程详情失败", e)
            }
        }
    }

    fun removeAttraction(attractionId: String) {
        val tripId = uiState.value.trip?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.removeAttractionFromTrip(tripId, attractionId).collect {
                when (it) {
                    is NetworkResult.Loading -> {
                        _uiState.update { currentState -> currentState.copy(isLoading = true) }
                    }
                    is NetworkResult.Success -> {
                        _uiState.update { currentState -> currentState.copy(isLoading = false, error = null) }
                        loadTripDetails(tripId) // 重新加载行程详情以反映变更
                    }
                    is NetworkResult.Error -> {
                        Logger.e("移除景点失败: ${it.message}")
                        _uiState.update { currentState -> currentState.copy(isLoading = false, error = it.message) }
                    }
                }
            }
        }
    }

    fun sortAttractionsIntelligently() {
        val trip = uiState.value.trip ?: return
        if (trip.attractions.size < 2) return

        viewModelScope.launch {
            try {
                val remainingAttractions = trip.attractions.toMutableList()
                val sortedAttractions = mutableListOf<TripAttraction>()

                var currentAttraction = remainingAttractions.removeFirst()
                sortedAttractions.add(currentAttraction)

                while (remainingAttractions.isNotEmpty()) {
                    var nearestAttraction: TripAttraction? = null
                    var minDistance = Double.MAX_VALUE

                    for (nextAttraction in remainingAttractions) {
                        val distance = calculateDistance(
                            currentAttraction.attraction.latitude,
                            currentAttraction.attraction.longitude,
                            nextAttraction.attraction.latitude,
                            nextAttraction.attraction.longitude
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
                    } ?: break
                }

                repository.reorderAttractionsInTrip(trip.id, sortedAttractions).collect {
                    when (it) {
                        is NetworkResult.Loading -> {
                            _uiState.update { currentState -> currentState.copy(isLoading = true) }
                        }
                        is NetworkResult.Success -> {
                            _uiState.update { currentState -> currentState.copy(isLoading = false, error = null) }
                            loadTripDetails(trip.id) // 重新加载行程详情以反映变更
                        }
                        is NetworkResult.Error -> {
                            Logger.e("重新排序景点失败: ${it.message}")
                            _uiState.update { currentState -> currentState.copy(isLoading = false, error = it.message) }
                        }
                    }
                }
            } catch (e: Exception) {
                Logger.e("排序景点失败", e)
                _uiState.update {
                    it.copy(error = e.message ?: "排序景点失败")
                }
            }
        }
    }

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

    fun toggleFavoriteAttraction(attraction: Attraction) {
        viewModelScope.launch {
            val userId = userRepository.getCurrentUser().firstOrNull()?.let { result ->
                if (result is NetworkResult.Success) result.data.id else null
            }
            if (userId == null) {
                _uiState.update { it.copy(error = "用户未登录，无法收藏") }
                return@launch
            }

            val isFavorite = _uiState.value.trip?.attractions?.any { it.attraction.id == attraction.id && it.attraction.isFavorite } ?: false

            if (isFavorite) {
                userRepository.removeFavoriteAttraction(attraction.id).collect {
                    when (it) {
                        is NetworkResult.Success -> {
                            _uiState.update { currentState ->
                                val updatedTrip = currentState.trip?.copy(
                                    attractions = currentState.trip.attractions.map { tripAttraction ->
                                        if (tripAttraction.attraction.id == attraction.id) {
                                            tripAttraction.copy(attraction = tripAttraction.attraction.copy(isFavorite = false))
                                        } else {
                                            tripAttraction
                                        }
                                    }
                                )
                                currentState.copy(trip = updatedTrip, error = null)
                            }
                        }
                        is NetworkResult.Error -> {
                            Logger.e("移除收藏失败: ${it.message}")
                            _uiState.update { currentState -> currentState.copy(error = it.message) }
                        }
                        is NetworkResult.Loading -> { /* Handle loading state if needed */ }
                    }
                }
            } else {
                userRepository.addFavoriteAttraction(attraction.id).collect {
                    when (it) {
                        is NetworkResult.Success -> {
                            _uiState.update { currentState ->
                                val updatedTrip = currentState.trip?.copy(
                                    attractions = currentState.trip.attractions.map { tripAttraction ->
                                        if (tripAttraction.attraction.id == attraction.id) {
                                            tripAttraction.copy(attraction = tripAttraction.attraction.copy(isFavorite = true))
                                        } else {
                                            tripAttraction
                                        }
                                    }
                                )
                                currentState.copy(trip = updatedTrip, error = null)
                            }
                        }
                        is NetworkResult.Error -> {
                            Logger.e("添加收藏失败: ${it.message}")
                            _uiState.update { currentState -> currentState.copy(error = it.message) }
                        }
                        is NetworkResult.Loading -> { /* Handle loading state if needed */ }
                    }
                }
            }
        }
    }
}