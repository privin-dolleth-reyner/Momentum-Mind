@file:OptIn(ExperimentalMaterial3Api::class)

package com.privin.mm

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.privin.mm.ui.theme.MomentumMindTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

        Scaffold(
            snackbarHost = { SnackbarHost(snackBarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.app_name),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors()
                        .copy(containerColor = MaterialTheme.colorScheme.primaryContainer)
                )
            },
            bottomBar = {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ) {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route
                        val screens = listOf(Screen.Home, Screen.Favorites)
                        screens.forEach { screen ->
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        screen.icon,
                                        contentDescription = screen.label,
                                        modifier = Modifier.padding(
                                            vertical = 4.dp,
                                            horizontal = 16.dp
                                        )
                                    )
                                },
                                label = {
                                    Text(
                                        screen.label,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                },
                                selected = currentRoute == screen.route,
                                colors = NavigationBarItemDefaults.colors().copy(
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                                    selectedIndicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            NavigationGraph(navController, Modifier.padding(innerPadding)) {
                scope.launch {
                    snackBarHostState.showSnackbar(
                        message = it,
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    viewModel: MainViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    showSnackBar: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val value = state) {
            is MainState.Loaded -> {
                Column {
                    Text(
                        text = stringResource(R.string.home_title),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(16.dp)
                    )

                    LargeQuoteCard(
                        quote = value.quote,
                        onFavoriteClick = { isFav ->
                            viewModel.addQuoteToFavorites(value.quote.copy(isFavorite = isFav))
                        }
                    )
                }

            }

            is MainState.Loading -> {
                Loading()
            }

            is MainState.Error -> {
                showSnackBar(value.message)
            }
        }
    }
}

@Composable
fun Loading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.loading),
            modifier = modifier
        )
    }
}


@Composable
fun LargeQuoteCard(
    quote: Quote,
    modifier: Modifier = Modifier,
    onFavoriteClick: (isFavorite: Boolean) -> Unit = {},
) {


    var quoteTextSize by remember { mutableStateOf(48.sp) }
    var authorTextSize by remember { mutableStateOf(24.sp) }

    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Box(
            modifier = Modifier
                .padding(24.dp),
            contentAlignment = Alignment.BottomCenter
        ) {

            Text(
                text = "❝",
                style = TextStyle(
                    fontSize = 200.sp,
                    color = Color.White,
                    fontFamily = FontFamily.Serif
                ),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = (-20).dp, y = (-40).dp)
                    .alpha(0.1f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AutoResizingText(
                    text = quote.quote,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    initialFontSize = quoteTextSize,
                    onFontSizeChanged = { quoteTextSize = it },
                    style = MaterialTheme.typography.displayLarge.copy(textAlign = TextAlign.Center)
                )

                AutoResizingText(
                    text = "— ${quote.author}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    initialFontSize = authorTextSize,
                    onFontSizeChanged = { authorTextSize = it },
                    style = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.Center),
                    maxLines = 2
                )

                Spacer(Modifier.height(16.dp))

                ShareAndFavorite(quote, onFavoriteClick)
            }
        }
    }
}

@Composable
private fun ShareAndFavorite(
    quote: Quote,
    onFavoriteClick: (isFavorite: Boolean) -> Unit
) {
    val context = LocalContext.current
    val isFavorite = remember { mutableStateOf(quote.isFavorite) }
    val toastMessage =
        if (isFavorite.value.not()) stringResource(R.string.favorites_added_to_favorites) else stringResource(
            R.string.favorites_removed_favorites
        )
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        IconButton(onClick = {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "${quote.quote} \n - ${quote.author}")
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share via"))
        }) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = stringResource(R.string.favorites_share_quote),
            )
        }

        IconButton(onClick = {
            isFavorite.value = quote.isFavorite.not()
            onFavoriteClick(isFavorite.value)
            Toast.makeText(
                context,
                toastMessage,
                Toast.LENGTH_SHORT
            ).show()
        }) {
            Icon(
                imageVector = if (isFavorite.value) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = stringResource(R.string.favorites_favorite_quote),
                tint = if (isFavorite.value) Color.Red else LocalContentColor.current
            )
        }
    }
}

@Composable
private fun AutoResizingText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle,
    initialFontSize: TextUnit,
    onFontSizeChanged: (TextUnit) -> Unit,
    maxLines: Int = 6
) {
    var fontSize by remember { mutableStateOf(initialFontSize) }

    Text(
        text = text,
        modifier = modifier,
        style = style.copy(fontSize = fontSize),
        maxLines = maxLines,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.hasVisualOverflow) {
                fontSize *= 0.9f
                onFontSizeChanged(fontSize)
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun QuoteScreenPreview() {
    MomentumMindTheme {
        LargeQuoteCard(
            Quote(
                quote = "Where there is will there is a way",
                author = "George Herbert"
            )
        )
    }
}