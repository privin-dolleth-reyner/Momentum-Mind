package com.privin.gpt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privin.data.QuotesRepository
import com.privin.data.models.Quote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: QuotesRepository
): ViewModel() {
    private val _state = MutableStateFlow<MainState>(MainState.Loading)
    val state = _state.asStateFlow()

    fun getTodayPrice() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getDailyQuote().collect {
                _state.value = MainState.Loaded(it)
            }
        }
    }
}

sealed class MainState {
    data object Loading: MainState()
    data class Loaded(val quote: Quote): MainState()
}
