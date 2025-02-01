package com.privin.mm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privin.data.QuotesRepository
import com.privin.data.models.Quote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val quotesRepository: QuotesRepository
): ViewModel() {

    private val _state = MutableStateFlow(State())
    val state = _state

    private val dispatcherContext = Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _state.value = _state.value.copy(uiState = FavouritesState.Loaded(emptyList()))
    }

    init {
        loadFavourites()
    }

    fun setSelectedQuote(quote: Quote) {
        _state.value = _state.value.copy(selectedQuote = quote)
    }

    fun resetSelectedQuote() {
        _state.value = _state.value.copy(selectedQuote = null)
    }

    private fun loadFavourites() {
        viewModelScope.launch(dispatcherContext) {
            quotesRepository.getFavourites().collect{
                _state.value =  _state.value.copy(uiState = FavouritesState.Loaded(it))
            }
        }
    }

    fun updateQuoteFavorites(quote: Quote, isFavorite: Boolean) {
        viewModelScope.launch(dispatcherContext) {
            quotesRepository.updateFavorites(quote.copy(isFavorite = isFavorite))
        }
    }
}

data class State(
    val uiState: FavouritesState = FavouritesState.Loading,
    val selectedQuote: Quote? = null
)

sealed interface FavouritesState {
    data object Loading : FavouritesState
    data class Loaded(val quotes: List<Quote>) : FavouritesState
}