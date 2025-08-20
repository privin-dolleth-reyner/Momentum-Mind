package com.privin.mm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privin.data.Error
import com.privin.data.QuotesRepository
import com.privin.data.Result
import com.privin.data.models.Quote
import dagger.hilt.android.lifecycle.HiltViewModel
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

    init {
        getDailyQuote()
    }

    private fun getDailyQuote() {
        viewModelScope.launch {
            repository.getDailyQuote().collect { result ->
                when(result){
                    is Result.Failure -> handleError(result.error)
                    is Result.Success<Quote> -> _state.value = MainState.Loaded(result.data)
                }
            }
        }
    }

    private fun handleError(error: Error) {
        when(error){
            is Error.ErrorResponse -> _state.value = MainState.Error(error.error ?: "Something went wrong, please try again later")
            is Error.NoInternet -> _state.value = MainState.Error("No internet connection")
            is Error.Unexpected -> _state.value = MainState.Error(error.msg ?: "Something went wrong, please try again later")
        }
    }

    fun addQuoteToFavorites(quote: Quote) {
        viewModelScope.launch {
            repository.updateFavorites(quote)
        }
    }
}

sealed class MainState {
    data object Loading : MainState()
    data class Loaded(val quote: Quote) : MainState()
    data class Error(val message: String) : MainState()
}
