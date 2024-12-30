package com.privin.gpt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privin.data.QuotesRepository
import com.privin.data.models.Quote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(private val quotesRepository: QuotesRepository): ViewModel() {

    init {
        loadFavourites()
    }

    val state = MutableStateFlow<FavouritesState>(FavouritesState.Loading)

    private fun loadFavourites() {
        viewModelScope.launch(Dispatchers.IO) {
            state.value = FavouritesState.Loaded(quotesRepository.getFavourites())
        }
    }

    fun removeQuoteFromFavorites(quote: Quote) {
        viewModelScope.launch(Dispatchers.IO) {
            quotesRepository.updateFavorites(quote.copy(isFavorite = false))
        }
    }
}


sealed interface FavouritesState {
    data object Loading : FavouritesState
    data class Loaded(val quotes: Flow<List<Quote>>) : FavouritesState
}