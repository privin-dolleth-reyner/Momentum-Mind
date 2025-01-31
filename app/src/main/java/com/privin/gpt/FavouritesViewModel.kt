package com.privin.gpt

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

    private val _state = MutableStateFlow<FavouritesState>(FavouritesState.Loading)
    val state = _state

    private val dispatcherContext = Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _state.value = FavouritesState.Loaded(emptyList())
    }

    init {
        loadFavourites()
    }

    private fun loadFavourites() {
        viewModelScope.launch(dispatcherContext) {
            quotesRepository.getFavourites().collect{
                _state.value = FavouritesState.Loaded(it)
            }
        }
    }

    fun removeQuoteFromFavorites(quote: Quote) {
        viewModelScope.launch(dispatcherContext) {
            quotesRepository.updateFavorites(quote.copy(isFavorite = false))
        }
    }
}


sealed interface FavouritesState {
    data object Loading : FavouritesState
    data class Loaded(val quotes: List<Quote>) : FavouritesState
}