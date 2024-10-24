@file:OptIn(ExperimentalMaterial3Api::class)

package com.privin.gpt

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.privin.data.models.Quote
import com.privin.gpt.ui.theme.GPTTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
        setContent {
            QuoteApp()
        }
    }
}

@Composable
fun QuoteApp() {
    GPTTheme {
        val navController = rememberNavController()
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "GPT Quotes") }, colors = TopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.secondary
                    )
                )
            },
            bottomBar = {
                BottomAppBar {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )  {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route

                        val screens = listOf(Screen.Home, Screen.Favorites)

                        screens.forEach { screen ->
                            NavigationBarItem(
                                icon = { Icon(screen.icon, contentDescription = screen.label) },
                                label = { Text(screen.label) },
                                selected = currentRoute == screen.route,
                                colors = NavigationBarItemDefaults.colors().copy(
                                    selectedIndicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedIconColor = MaterialTheme.colorScheme.primary
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
            NavigationGraph(navController, Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun HomeScreen(
    viewModel: MainViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val value = state) {
            is MainState.Loaded -> {
                FullscreenQuoteCard(value.quote){ isFav ->
                    viewModel.addQuoteToFavorites(value.quote.copy(isFavorite = isFav))
                }
            }

            is MainState.Loading -> {
                Loading()
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
        Text(
            text = "Loading...",
            modifier = modifier
        )
    }
}

@Composable
fun FullscreenQuoteCard(
    quote: Quote,
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(
        Color(0xFF1F1F1F),
        Color(0xFF3E3E3E)
    ),
    onFavoriteClick: (isFavorite: Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    var isFavorite by remember { mutableStateOf(quote.isFavorite) }

    var quoteTextSize by remember { mutableStateOf(48.sp) }
    var authorTextSize by remember { mutableStateOf(24.sp) }

    Text(
        text = "Today's Quote",
        textAlign = TextAlign.Center,
        fontSize = 28.sp,
        color = MaterialTheme.colorScheme.tertiary,
        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
        fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
        lineHeight = TextUnit(10f, TextUnitType.Sp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp)
    )

    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(gradientColors)
                )
                .padding(24.dp)
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
                        .padding(bottom = 32.dp),
                    initialFontSize = quoteTextSize,
                    onFontSizeChanged = { quoteTextSize = it },
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Serif
                    )
                )

                AutoResizingText(
                    text = "— ${quote.author}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    initialFontSize = authorTextSize,
                    onFontSizeChanged = { authorTextSize = it },
                    style = TextStyle(
                        fontStyle = FontStyle.Italic,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Serif
                    ),
                    maxLines = 2
                )

                Spacer(Modifier.height(16.dp))

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
                            contentDescription = "Share Quote",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }

                    IconButton(onClick = {
                        isFavorite = !isFavorite
                        onFavoriteClick(isFavorite)
                        Toast.makeText(
                            context,
                            if (isFavorite) "Added to Favorites" else "Removed from Favorites",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite Quote",
                            tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
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
    GPTTheme {
        FullscreenQuoteCard(Quote(quote = "Where there is will there is a way", author = "George Herbert"))
    }
}