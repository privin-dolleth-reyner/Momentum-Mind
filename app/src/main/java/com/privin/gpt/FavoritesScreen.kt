package com.privin.gpt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.privin.data.models.Quote
import androidx.compose.foundation.lazy.items

@Composable
fun FavoritesScreen(viewModel: FavouritesViewModel = hiltViewModel()){

    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Favorites",
            style = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.tertiary,
                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                lineHeight = 1.em
            ),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
        )

        when (val value = state){
            is FavouritesState.Loading -> Loading()
            is FavouritesState.Loaded -> MotivationalQuotesList(initialQuotes = value.quotes){
                viewModel.removeQuoteFromFavorites(it)
            }
        }

    }

}


@Composable
fun MotivationalQuotesList(
    modifier: Modifier = Modifier,
    initialQuotes: List<Quote> = emptyList(),
    onQuoteRemoved: (Quote) -> Unit = {}
) {
    var quotes by remember { mutableStateOf(initialQuotes) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(quotes) { quote ->
            QuoteCard(
                quote = quote,
                onRemove = {
                    quotes = quotes.filter { it.date != quote.date }
                    onQuoteRemoved(quote)
                }
            )
        }
    }
}

@Composable
private fun QuoteCard(
    quote: Quote,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = "- ${quote.author}",
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove quote",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
