package com.privin.gpt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privin.data.GoldRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: GoldRepository
): ViewModel() {
    private val _state = MutableStateFlow<MainState>(MainState.Loading)
    val state = _state.asStateFlow()

    fun getTodayPrice() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getTodayPrice().collect {
                _state.value = MainState.Loaded(it)
            }
        }
    }
}

sealed class MainState {
    data object Loading: MainState()
    data class Loaded(val price: Double): MainState()
}
