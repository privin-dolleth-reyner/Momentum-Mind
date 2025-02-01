package com.privin.mm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privin.data.QuotesRepository
import com.privin.data.models.Quote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: QuotesRepository
) : ViewModel() {
    private val _state = MutableStateFlow<MainState>(MainState.Loading)
    val state = _state.asStateFlow()

    private val dispatcherContext = Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _state.value = MainState.Error("Something went wrong, please try again later")
    }

    init {
        getDailyQuote()
    }

    private fun getDailyQuote() {
        viewModelScope.launch(dispatcherContext) {
            repository.getDailyQuote().collect {
                if (it.isEmpty().not()){
                    _state.value = MainState.Loaded(it)
                }
            }
        }
    }

    fun addQuoteToFavorites(quote: Quote) {
        viewModelScope.launch(dispatcherContext) {
            repository.updateFavorites(quote)
        }
    }
}

sealed class MainState {
    data object Loading : MainState()
    data class Loaded(val quote: Quote) : MainState()
    data class Error(val message: String) : MainState()
}
