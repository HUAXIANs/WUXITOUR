package com.example.wuxitour.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wuxitour.data.common.NetworkResult
import com.example.wuxitour.data.model.Attraction
import com.example.wuxitour.data.model.Review
import com.example.wuxitour.data.model.Trip
import com.example.wuxitour.data.model.User
import com.example.wuxitour.data.repository.AttractionRepository
import com.example.wuxitour.data.repository.TripRepository
import com.example.wuxitour.data.repository.UserRepository
import com.example.wuxitour.utils.DateTimeUtils
import com.example.wuxitour.utils.Logger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

data class AttractionDetailUiState(
    val isLoading: Boolean = true,
    val attraction: Attraction? = null,
    val isFavorited: Boolean = false,
    val userTrips: List<Trip> = emptyList(),
    val showAddToTripDialog: Boolean = false,
    val error: String? = null,
    val user: User? = null
)

class AttractionDetailViewModel(
    private val attractionRepository: AttractionRepository,
    private val tripRepository: TripRepository,
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(AttractionDetailUiState())
    val uiState: StateFlow<AttractionDetailUiState> = _uiState.asStateFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    init {
        loadAttraction()
        loadUserTrips()
    }

    fun loadAttraction() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val attractionId = savedStateHandle.get<String>("attractionId")
                    ?: throw IllegalArgumentException("Attraction ID is required")

                attractionRepository.getAttractionDetail(attractionId).collect { result ->
                    when (result) {
                        is NetworkResult.Loading<Attraction> -> _uiState.update { it.copy(isLoading = true) }
                        is NetworkResult.Success<Attraction> -> {
                            val attraction = result.data
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    attraction = attraction,
                                    isFavorited = attraction.isFavorite,
                                    error = null
                                )
                            }
                        }
                        is NetworkResult.Error<Attraction> -> {
                            _uiState.update { it.copy(isLoading = false, error = result.message) }
                            Logger.e("加载景点详情失败: ${result.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "加载失败") }
                Logger.e("加载景点详情失败", e)
            }
        }
    }

    private fun loadUserTrips() {
        viewModelScope.launch {
            try {
                userRepository.getUserTrips().collect { result ->
                    when (result) {
                        is NetworkResult.Success<List<Trip>> -> {
                            _uiState.update { it.copy(userTrips = result.data) }
                        }
                        is NetworkResult.Error<List<Trip>> -> {
                            Logger.e("加载用户行程失败: ${result.message}")
                        }
                        is NetworkResult.Loading<List<Trip>> -> { /* Handle loading state if needed */ }
                    }
                }
            } catch (e: Exception) {
                Logger.e("加载用户行程失败", e)
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _uiState.value.attraction?.let { currentAttraction ->
                val userId = userRepository.getCurrentUser().firstOrNull()?.let { result ->
                    if (result is NetworkResult.Success) result.data.id else null
                }
                if (userId == null) {
                    _userMessage.emit("用户未登录，无法收藏")
                    return@let
                }

                val newFavoriteStatus = !currentAttraction.isFavorite
                val actionFlow = if (newFavoriteStatus) {
                    userRepository.addFavoriteAttraction(currentAttraction.id)
                } else {
                    userRepository.removeFavoriteAttraction(currentAttraction.id)
                }

                actionFlow.collect { result ->
                    when (result) {
                        is NetworkResult.Success<Boolean> -> {
                            _uiState.update { uiState ->
                                uiState.copy(
                                    attraction = uiState.attraction?.copy(isFavorite = newFavoriteStatus),
                                    isFavorited = newFavoriteStatus
                                )
                            }
                            _userMessage.emit(if (newFavoriteStatus) "已收藏" else "已取消收藏")
                        }
                        is NetworkResult.Error<Boolean> -> {
                            _userMessage.emit(result.message)
                            Logger.e("切换收藏状态失败: ${result.message}")
                        }
                        is NetworkResult.Loading<Boolean> -> { /* Handle loading state if needed */ }
                    }
                }
            }
        }
    }

    fun showAddToTripDialog(show: Boolean) {
        _uiState.update { it.copy(showAddToTripDialog = show) }
        if (show) loadUserTrips()
    }

    fun addAttractionToTrip(tripId: String) {
        val attractionId = uiState.value.attraction?.id
        if (attractionId != null) {
            viewModelScope.launch {
                try {
                    tripRepository.addAttractionToTrip(tripId, attractionId).collect { result ->
                        when (result) {
                            is NetworkResult.Success<Boolean> -> {
                                val tripResult = tripRepository.getTripDetail(tripId).first()
                                if (tripResult is NetworkResult.Success<Trip>) {
                                    _userMessage.emit("已成功添加到行程: ${tripResult.data.name}")
                                } else {
                                    _userMessage.emit("已成功添加到行程")
                                }
                                showAddToTripDialog(false)
                            }
                            is NetworkResult.Error<Boolean> -> {
                                _userMessage.emit(result.message)
                                Logger.e("添加景点到行程失败: ${result.message}")
                            }
                            is NetworkResult.Loading<Boolean> -> { /* Handle loading state if needed */ }
                        }
                    }
                } catch (e: Exception) {
                    _userMessage.emit("添加景点到行程失败")
                    Logger.e("添加景点到行程失败", e)
                }
            }
        }
    }
}