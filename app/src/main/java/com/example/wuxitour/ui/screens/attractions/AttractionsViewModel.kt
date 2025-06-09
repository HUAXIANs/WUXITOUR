package com.example.wuxitour.ui.screens.attractions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wuxitour.data.model.Attraction
import com.example.wuxitour.data.model.AttractionCategory
import com.example.wuxitour.data.repository.MockDataRepository
import kotlinx.coroutines.flow.*

data class AttractionsUiState(
    val isLoading: Boolean = false,
    val allAttractions: List<Attraction> = emptyList(), // 保存原始数据
    val categories: List<AttractionCategory> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: AttractionCategory? = null,
    val error: String? = null
)

class AttractionsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AttractionsUiState())
    val uiState: StateFlow<AttractionsUiState> = _uiState.asStateFlow()

    // 新增：一个只暴露“被筛选后”的景点列表的StateFlow
    val filteredAttractions: StateFlow<List<Attraction>> =
        _uiState.map { state ->
            state.allAttractions.filter { attraction ->
                val matchesCategory = state.selectedCategory == null || attraction.category == state.selectedCategory
                val matchesQuery = state.searchQuery.isBlank() ||
                        attraction.name.contains(state.searchQuery, ignoreCase = true) ||
                        attraction.description.contains(state.searchQuery, ignoreCase = true)
                matchesCategory && matchesQuery
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // 5秒后如果没有订阅者则停止
            initialValue = emptyList()
        )

    init {
        loadData()
    }

    private fun loadData() {
        _uiState.update { it.copy(isLoading = true) }
        val attractions = MockDataRepository.getMockAttractions()
        val categories = AttractionCategory.values().toList()
        _uiState.update {
            it.copy(
                isLoading = false,
                allAttractions = attractions,
                categories = categories
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onCategorySelected(category: AttractionCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun refresh() {
        loadData()
    }
}