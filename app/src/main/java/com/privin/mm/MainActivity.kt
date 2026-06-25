@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.privin.mm

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.privin.data.models.Quote
import com.privin.mm.ui.components.AnimatedFavoriteButton
import com.privin.mm.ui.theme.AnimatedGradientBackground
import com.privin.mm.ui.theme.MomentumMindTheme
import com.privin.mm.ui.theme.quoteCardBrush
import com.privin.mm.ui.util.QuoteImageShareHost
import com.privin.mm.ui.util.shareQuoteText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
        setContent {
            QuoteApp()
        }
    }
}

@Composable
fun QuoteApp() {
    MomentumMindTheme {
        val navController = rememberNavController()
        val snackBarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        AnimatedGradientBackground {
            Scaffold(
                containerColor = Color.Transparent,
                snackbarHost = { SnackbarHost(snackBarHostState) },
                bottomBar = {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp,
                    ) {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route
                        val screens = listOf(Screen.Home, Screen.Favorites)
                        screens.forEach { screen ->
                            val selected = currentRoute == screen.route
                            val iconScale by animateFloatAsState(
                                targetValue = if (selected) 1.18f else 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium,
                                ),
                                label = "navIconScale",
                            )
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        screen.icon,
                                        contentDescription = screen.label,
                                        modifier = Modifier.scale(iconScale),
                                    )
                                },
                                label = {
                                    Text(screen.label, style = MaterialTheme.typography.labelMedium)
                                },
                                selected = selected,
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                    selectedTextColor = MaterialTheme.colorScheme.onBackground,
                                    unselectedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                    indicatorColor = MaterialTheme.colorScheme.primary,
                                ),
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxSize(),
            ) { innerPadding ->
                NavigationGraph(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding),
                    showSnackbar = {
                        scope.launch {
                            snackBarHostState.showSnackbar(
                                message = it,
                                withDismissAction = true,
                                duration = SnackbarDuration.Long,
                            )
                        }
                    },
                    showUndoSnackbar = { message, actionLabel, onUndo ->
                        scope.launch {
                            // Replace any visible snackbar, then keep this one up for a
                            // 30-second undo window before letting the removal stand.
                            snackBarHostState.currentSnackbarData?.dismiss()
                            val result = withTimeoutOrNull(30_000L) {
                                snackBarHostState.showSnackbar(
                                    message = message,
                                    actionLabel = actionLabel,
                                    withDismissAction = true,
                                    duration = SnackbarDuration.Indefinite,
                                )
                            }
                            if (result == SnackbarResult.ActionPerformed) onUndo()
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    viewModel: MainViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    showSnackBar: (String) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var shareRequest by remember { mutableStateOf<Quote?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        when (val value = state) {
            is MainState.Loaded -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.home_title),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(16.dp),
                    )

                    QuoteReveal(modifier = Modifier.fillMaxSize()) {
                        LargeQuoteCard(
                            quote = value.quote,
                            onFavoriteClick = { isFav ->
                                viewModel.addQuoteToFavorites(value.quote.copy(isFavorite = isFav))
                            },
                            onShareImage = { shareRequest = value.quote },
                        )
                    }
                }
            }

            is MainState.Loading -> Loading()

            is MainState.Error -> showSnackBar(value.message)
        }

        QuoteImageShareHost(request = shareRequest, onComplete = { shareRequest = null })
    }
}

/** Wraps content in a one-shot fade + slide-up + scale entrance. */
@Composable
private fun QuoteReveal(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(animationSpec = tween(450)) +
            slideInVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow,
                ),
                initialOffsetY = { it / 6 },
            ) +
            scaleIn(initialScale = 0.92f, animationSpec = tween(450)),
    ) {
        content()
    }
}

@Composable
fun Loading(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "loading")
    val pulse by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "loadingPulse",
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
                .size(96.dp)
                .scale(pulse)
                .alpha(pulse.coerceAtMost(1f)),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.loading),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        )
    }
}

@Composable
fun LargeQuoteCard(
    quote: Quote,
    modifier: Modifier = Modifier,
    onFavoriteClick: (isFavorite: Boolean) -> Unit = {},
    onShareImage: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(
                brush = quoteCardBrush(),
                shape = MaterialTheme.shapes.extraLarge,
            ),
    ) {
        Text(
            text = "❝",
            style = TextStyle(
                fontSize = 200.sp,
                color = Color.White,
                fontFamily = FontFamily.Serif,
            ),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp)
                .alpha(0.12f),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AutoResizingText(
                text = quote.quote,
                modifier = Modifier.fillMaxWidth(),
                initialFontSize = 44.sp,
                style = MaterialTheme.typography.displayLarge.copy(
                    color = Color.White,
                    textAlign = TextAlign.Center,
                ),
            )

            // Accent divider between quote and author.
            Box(
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .width(44.dp)
                    .height(2.dp)
                    .background(Color.White.copy(alpha = 0.6f)),
            )

            AutoResizingText(
                text = quote.author,
                modifier = Modifier.fillMaxWidth(),
                initialFontSize = 22.sp,
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Color.White.copy(alpha = 0.92f),
                    textAlign = TextAlign.Center,
                ),
                maxLines = 2,
                minFontSize = 14.sp,
            )

            Spacer(Modifier.height(24.dp))

            QuoteActions(
                quote = quote,
                onFavoriteClick = onFavoriteClick,
                onShareImage = onShareImage,
            )
        }
    }
}

@Composable
private fun QuoteActions(
    quote: Quote,
    onFavoriteClick: (isFavorite: Boolean) -> Unit,
    onShareImage: () -> Unit,
) {
    val context = LocalContext.current
    val addedMsg = stringResource(R.string.favorites_added_to_favorites)
    val removedMsg = stringResource(R.string.favorites_removed_favorites)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        // Tap → share as image, long-press → share as plain text.
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .combinedClickable(
                    onClick = onShareImage,
                    onLongClick = { shareQuoteText(context, quote) },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = stringResource(R.string.favorites_share_quote),
                tint = Color.White,
            )
        }

        AnimatedFavoriteButton(
            isFavorite = quote.isFavorite,
            contentDescription = stringResource(R.string.favorites_favorite_quote),
            inactiveTint = Color.White,
            onToggle = { nowFavorite ->
                onFavoriteClick(nowFavorite)
                Toast.makeText(
                    context,
                    if (nowFavorite) addedMsg else removedMsg,
                    Toast.LENGTH_SHORT,
                ).show()
            },
        )
    }
}

@Composable
private fun AutoResizingText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle,
    initialFontSize: TextUnit,
    maxLines: Int = 6,
    minFontSize: TextUnit = 18.sp,
) {
    var fontSize by remember(text) { mutableStateOf(initialFontSize) }
    var readyToDraw by remember(text) { mutableStateOf(false) }

    Text(
        text = text,
        modifier = modifier.alpha(if (readyToDraw) 1f else 0f),
        style = style.copy(fontSize = fontSize),
        maxLines = maxLines,
        onTextLayout = { result ->
            if (result.hasVisualOverflow && fontSize.value > minFontSize.value) {
                fontSize = (fontSize.value * 0.92f).coerceAtLeast(minFontSize.value).sp
            } else {
                readyToDraw = true
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun QuoteScreenPreview() {
    MomentumMindTheme {
        LargeQuoteCard(
            Quote(
                quote = "Where there is will there is a way",
                author = "George Herbert",
            ),
        )
    }
}
