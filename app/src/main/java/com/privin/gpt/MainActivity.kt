@file:OptIn(ExperimentalMaterial3Api::class)

package com.privin.gpt

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.privin.data.models.Quote
import com.privin.gpt.ui.theme.GPTTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuoteApp()
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getTodayPrice()
    }

}

@Composable
fun QuoteApp(){
    GPTTheme {
        var selectedItemIndex by remember { mutableIntStateOf(0) }
        Scaffold(
            topBar = { TopAppBar(title = { Text(text = "GPT Quotes") }, colors = TopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.secondary
            )) },
            bottomBar = { BottomAppBar {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer, contentColor = MaterialTheme.colorScheme.onPrimary) {
                   NavigationBarItem(icon = { Icon(imageVector = Icons.Filled.Home, contentDescription = "Home") }, label = { Text(text = "Home") }, selected = selectedItemIndex == 0, onClick = { selectedItemIndex = 0}, colors = NavigationBarItemDefaults.colors().copy(selectedIndicatorColor = MaterialTheme.colorScheme.primaryContainer, selectedIconColor = MaterialTheme.colorScheme.primary))
                   NavigationBarItem(icon = { Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = "Quotes") }, label = { Text(text = "Quotes") }, selected = selectedItemIndex == 1, onClick = { selectedItemIndex = 1}, colors = NavigationBarItemDefaults.colors().copy(selectedIndicatorColor = MaterialTheme.colorScheme.primaryContainer, selectedIconColor = MaterialTheme.colorScheme.primary))
                   NavigationBarItem(icon = { Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings")}, label = { Text(text = "Settings") }, selected = selectedItemIndex == 2, onClick = { selectedItemIndex = 2}, colors = NavigationBarItemDefaults.colors().copy(selectedIndicatorColor = MaterialTheme.colorScheme.primaryContainer, selectedIconColor = MaterialTheme.colorScheme.primary))
                }
            } },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            HomeScreen(innerPadding)
        }
    }
}

@Composable
fun HomeScreen(innerPadding: PaddingValues, viewModel: MainViewModel = hiltViewModel(), modifier: Modifier = Modifier){
    val state = viewModel.state.collectAsState()
    Column(modifier = modifier.fillMaxWidth().padding(innerPadding).background(color = MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        when (val value = state.value) {
            is MainState.Loaded -> {
                QuoteScreen(value.quote)
            }
            is MainState.Loading -> {
                Loading(
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }

}

@Composable
fun Loading(modifier: Modifier = Modifier) {
    Column (modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
        Text(
            text = "Loading...",
            modifier = modifier
        )
    }
}

@Composable
fun QuoteScreen(quote: Quote, onFavoriteClick: (isFavorite: Boolean) -> Unit = {}) {
    val context = LocalContext.current
    var isFavorite by remember { mutableStateOf(quote.isFavorite) }

    Text(
        text = "Today's Quote",
        textAlign = TextAlign.Center,
        fontSize = 28.sp,
        color = MaterialTheme.colorScheme.tertiary,
        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
        fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
        lineHeight = TextUnit(80f, TextUnitType.Sp),
        modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(16.dp) // Occupies most of the space
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Text(
                text = quote.quote,
                textAlign = TextAlign.Center,
                fontSize = 36.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                lineHeight = TextUnit(80f, TextUnitType.Sp),
                modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(16.dp).weight(2f) // Occupies most of the space
            )
            Text(
                text = "- ${quote.author}",
                textAlign = TextAlign.End,
                fontSize = 36.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                lineHeight = TextUnit(80f, TextUnitType.Sp),
                modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = 16.dp).weight(1f) // Occupies most of the space
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
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

@Preview(showBackground = true)
@Composable
fun QuoteScreenPreview() {
    GPTTheme {
        QuoteScreen(Quote(quote = "Where there is will there is a way", author = ""))
    }
}