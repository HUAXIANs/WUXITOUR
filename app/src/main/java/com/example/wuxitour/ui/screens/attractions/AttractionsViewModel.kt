package com.example.wuxitour.ui.screens.attractions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wuxitour.data.model.Attraction
import com.example.wuxitour.data.model.AttractionCategory
import com.example.wuxitour.data.repository.MockDataRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AttractionsUiState(
    val attractions: List<Attraction> = emptyList(),
    val categories: List<AttractionCategory> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: AttractionCategory? = null
)

class AttractionsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AttractionsUiState())
    val uiState: StateFlow<AttractionsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _uiState.update {
            it.copy(
                attractions = MockDataRepository.getMockAttractions(),
                categories = AttractionCategory.values().toList()
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onCategorySelected(category: AttractionCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }
}