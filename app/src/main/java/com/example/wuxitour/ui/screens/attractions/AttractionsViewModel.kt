package com.example.wuxitour.ui.screens.attractions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wuxitour.data.model.Attraction
import com.example.wuxitour.data.repository.AttractionRepository
import com.example.wuxitour.data.repository.UserRepository
import com.example.wuxitour.utils.Logger
import com.example.wuxitour.data.common.NetworkResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class AttractionsUiState(
    val isLoading: Boolean = false,
    val allAttractions: List<Attraction> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null
)

@HiltViewModel
class AttractionsViewModel @Inject constructor(
    private val repository: AttractionRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AttractionsUiState())
    val uiState: StateFlow<AttractionsUiState> = _uiState.asStateFlow()

    // 筛选后的景点列表
    val filteredAttractions: StateFlow<List<Attraction>> =
        _uiState.map { state ->
            state.allAttractions.filter { attraction ->
                state.searchQuery.isBlank() ||
                        (attraction.name.contains(state.searchQuery, ignoreCase = true)) ||
                        (attraction.category?.contains(state.searchQuery, ignoreCase = true) == true) ||
                        (attraction.tags?.any { tag -> tag.contains(state.searchQuery, ignoreCase = true) } == true)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                repository.getAttractions("景点").collect { result ->
                    when (result) {
                        is NetworkResult.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                        is NetworkResult.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    allAttractions = result.data,
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
                            Logger.e("加载景点列表失败: ${result.message}")
                        }
                        is NetworkResult.Empty -> {
                            _uiState.update { it.copy(isLoading = false, error = "没有找到景点数据") }
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
                Logger.e("加载景点列表失败", e)
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onFavoriteClick(attraction: Attraction) {
        viewModelScope.launch {
            val userId = userRepository.getCurrentUser().firstOrNull()?.let { result ->
                if (result is NetworkResult.Success) result.data.id else null
            }
            if (userId == null) {
                // 用户未登录，显示错误信息
                _uiState.update { it.copy(error = "用户未登录，无法收藏") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, error = null) }
            val newFavoriteStatus = !attraction.isFavorite

            val actionFlow = if (newFavoriteStatus) {
                userRepository.addFavoriteAttraction(attraction.id)
            } else {
                userRepository.removeFavoriteAttraction(attraction.id)
            }

            actionFlow.collect {
                when (it) {
                    is NetworkResult.Success -> {
                        _uiState.update {
                            currentState -> currentState.copy(
                                isLoading = false,
                                error = null,
                                allAttractions = currentState.allAttractions.map { item ->
                                    if (item.id == attraction.id) item.copy(isFavorite = newFavoriteStatus) else item
                                }
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        Logger.e("切换收藏状态失败: ${it.message}")
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
                        _uiState.update { currentState -> currentState.copy(isLoading = false, error = "操作完成，但无返回数据") }
                    }
                }
            }
        }
    }

    fun refresh() {
        loadData()
    }
}