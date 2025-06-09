package com.example.wuxitour.ui.screens.trip_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wuxitour.data.model.Trip
import com.example.wuxitour.data.repository.MockDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
            val trip = MockDataRepository.getTripById(tripId)
            if (trip != null) {
                _uiState.update { TripDetailUiState(isLoading = false, trip = trip) }
            } else {
                _uiState.update { TripDetailUiState(isLoading = false, error = "未找到该行程") }
            }
        }
    }
}