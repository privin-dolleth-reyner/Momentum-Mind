package com.privin.mm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.privin.data.models.Quote

@Composable
fun FavoritesScreen(viewModel: FavouritesViewModel = hiltViewModel()) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.favorites_title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        when (val value = state.uiState) {
            is FavouritesState.Loading -> Loading()
            is FavouritesState.Loaded -> FavoritesList(
                quotes = value.quotes,
                onSelect = { selectedQuote ->
                    viewModel.setSelectedQuote(selectedQuote)
                },
                updateQuoteFavourite = { quote, isFavorite ->
                    viewModel.updateQuoteFavorites(quote, isFavorite)
                }
            )
        }

    }

    state.selectedQuote?.let { selectedQuote ->
        Box(modifier = Modifier.fillMaxSize()) {

            LargeQuoteCard(
                quote = selectedQuote,
                onFavoriteClick = {
                    viewModel.updateQuoteFavorites(selectedQuote, it)
                }
            )


            IconButton(
                modifier = Modifier
                    .padding(24.dp)
                    .align(Alignment.TopEnd),
                onClick = {
                    viewModel.resetSelectedQuote()
                }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }

        }
    }
}

@Composable
fun EmptyScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.favorites_empty_screen),
            modifier = modifier
        )
    }
}

@Composable
fun FavoritesList(
    modifier: Modifier = Modifier,
    quotes: List<Quote>,
    updateQuoteFavourite: (Quote, Boolean) -> Unit,
    onSelect: (Quote) -> Unit = {},
) {

    if (quotes.isEmpty()) {
        EmptyScreen()
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(quotes) { quote ->
                QuoteCard(
                    quote = quote,
                    onRemove = {
                        updateQuoteFavourite(quote, false)
                    },
                    onClick = {
                        onSelect(quote)
                    }
                )
            }
        }
    }
}

@Composable
private fun QuoteCard(
    quote: Quote,
    onRemove: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = quote.quote,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "- ${quote.author}",
                    style = MaterialTheme.typography.titleMedium,
                    fontStyle = FontStyle.Italic,
                )
            }

            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.favorites_remove_quote),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview
@Composable
fun List() {
    FavoritesList(
        quotes = listOf(
            Quote(
                quote = "Where there is will there is a way",
                author = "George Herbert"
            )
        ),
        updateQuoteFavourite = { _, _ -> }
    )
}