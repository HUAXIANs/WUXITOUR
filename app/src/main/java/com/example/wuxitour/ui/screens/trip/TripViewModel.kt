package com.example.wuxitour.ui.screens.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wuxitour.data.model.Trip
import com.example.wuxitour.data.repository.MockDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TripUiState(
    val isLoading: Boolean = false,
    val trips: List<Trip> = emptyList(),
    val showCreateDialog: Boolean = false, // 新增：控制创建对话框的显示
    val error: String? = null
)

class TripViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TripUiState())
    val uiState: StateFlow<TripUiState> = _uiState.asStateFlow()

    init {
        loadTrips()
    }

    private fun loadTrips() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // 订阅行程列表的变化，实时更新UI
            MockDataRepository.tripsFlow.collectLatest { tripList ->
                _uiState.update {
                    it.copy(isLoading = false, trips = tripList)
                }
            }
        }
    }

    fun deleteTrip(id: String) {
        // Mock deletion - For now, we will just remove from the list.
        // In a real app, this would be a repository call.
    }

    // --- 新增的函数 ---
    fun showCreateTripDialog(show: Boolean) {
        _uiState.update { it.copy(showCreateDialog = show) }
    }

    fun createTrip(name: String, description: String) {
        MockDataRepository.createNewTrip(name, description)
        showCreateTripDialog(false) // 关闭对话框
    }
}