@file:OptIn(ExperimentalMaterial3Api::class)

package com.privin.mm

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.privin.data.models.Quote
import com.privin.mm.ui.theme.BLUE80
import com.privin.mm.ui.theme.FAV_CARD_DARK
import com.privin.mm.ui.theme.FAV_CARD_LIGHT
import com.privin.mm.ui.util.QuoteImageShareHost

@Composable
fun FavoritesScreen(
    viewModel: FavouritesViewModel = hiltViewModel(),
    showUndoSnackbar: (message: String, actionLabel: String, onUndo: () -> Unit) -> Unit = { _, _, _ -> },
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    var shareRequest by remember { mutableStateOf<Quote?>(null) }

    val removedMessage = stringResource(R.string.favorites_removed_favorites)
    val undoLabel = stringResource(R.string.undo)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.favorites_title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )

        when (val value = state.uiState) {
            is FavouritesState.Loading -> Loading()
            is FavouritesState.Loaded -> FavoritesList(
                quotes = value.quotes,
                onSelect = { viewModel.setSelectedQuote(it) },
                onRemove = { quote ->
                    // Remove immediately, then offer a 30s window to undo by
                    // re-flagging the same row as a favourite.
                    viewModel.updateQuoteFavorites(quote, false)
                    showUndoSnackbar(removedMessage, undoLabel) {
                        viewModel.updateQuoteFavorites(quote, true)
                    }
                },
            )
        }
    }

    // Animated full-screen detail. Keep the last quote so the exit can animate.
    val selected = state.selectedQuote
    var lastSelected by remember { mutableStateOf<Quote?>(null) }
    if (selected != null) lastSelected = selected

    AnimatedVisibility(
        visible = selected != null,
        enter = fadeIn(tween(220)) + scaleIn(initialScale = 0.9f, animationSpec = tween(260)),
        exit = fadeOut(tween(180)) + scaleOut(targetScale = 0.9f, animationSpec = tween(220)),
    ) {
        lastSelected?.let { detailQuote ->
            Box(modifier = Modifier.fillMaxSize()) {
                LargeQuoteCard(
                    quote = detailQuote,
                    onFavoriteClick = { viewModel.updateQuoteFavorites(detailQuote, it) },
                    onShareImage = { shareRequest = detailQuote },
                )
                IconButton(
                    modifier = Modifier
                        .padding(24.dp)
                        .align(Alignment.TopEnd),
                    onClick = { viewModel.resetSelectedQuote() },
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.favorites_close_detail),
                        tint = Color.White,
                    )
                }
            }
        }
    }

    QuoteImageShareHost(request = shareRequest, onComplete = { shareRequest = null })
}

@Composable
fun EmptyScreen(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "empty")
    val pulse by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "emptyPulse",
    )
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.app_logo),
            contentDescription = null,
            modifier = Modifier
                .size(88.dp)
                .scale(pulse)
                .alpha(0.85f),
        )
        Text(
            text = stringResource(R.string.favorites_empty_screen),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp, start = 32.dp, end = 32.dp),
        )
    }
}

@Composable
fun FavoritesList(
    modifier: Modifier = Modifier,
    quotes: List<Quote>,
    onRemove: (Quote) -> Unit,
    onSelect: (Quote) -> Unit = {},
) {
    if (quotes.isEmpty()) {
        EmptyScreen()
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(quotes, key = { it.date }) { quote ->
                SwipeableQuoteCard(
                    quote = quote,
                    modifier = Modifier.animateItem(),
                    onRemove = { onRemove(quote) },
                    onClick = { onSelect(quote) },
                )
            }
        }
    }
}

@Composable
private fun SwipeableQuoteCard(
    quote: Quote,
    modifier: Modifier = Modifier,
    onRemove: () -> Unit,
    onClick: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { target ->
            if (target == SwipeToDismissBoxValue.EndToStart) {
                onRemove()
                true
            } else {
                false
            }
        },
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val active = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart
            val iconScale by animateFloatAsState(
                targetValue = if (active) 1.25f else 0.9f,
                label = "deleteIconScale",
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.error, RoundedCornerShape(20.dp))
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.favorites_remove_quote),
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.scale(iconScale),
                )
            }
        },
    ) {
        QuoteCard(quote = quote, onClick = onClick)
    }
}

@Composable
private fun QuoteCard(
    quote: Quote,
    onClick: () -> Unit,
) {
    val dark = isSystemInDarkTheme()
    val containerColor = if (dark) FAV_CARD_DARK else FAV_CARD_LIGHT
    val quoteColor = if (dark) Color.White else Color(0xFF1A1A2E)
    val authorColor = if (dark) BLUE80 else MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = quote.quote,
                style = MaterialTheme.typography.titleLarge,
                color = quoteColor,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "— ${quote.author}",
                style = MaterialTheme.typography.titleMedium,
                fontStyle = FontStyle.Italic,
                color = authorColor,
            )
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
                author = "George Herbert",
            ),
        ),
        onRemove = { },
    )
}
