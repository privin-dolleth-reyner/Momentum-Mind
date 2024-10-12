package com.privin.gpt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
            GPTTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val state = viewModel.state.collectAsState()
                    when (val value = state.value) {
                        is MainState.Loaded -> {
                            DailyQuote(
                                quote = value.quote,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        is MainState.Loading -> {
                            DailyQuote(
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getTodayPrice()
    }

}

@Composable
fun DailyQuote(quote: Quote? = null, modifier: Modifier = Modifier) {
    if (quote == null){
        Text(
            text = "Loading...",
            modifier = modifier.basicMarquee()
        )
    }
    quote?.let {
        Text(
            text = it.quote,
            modifier = modifier
        )
    }

}

@Preview(showBackground = true)
@Composable
fun GoldPricePreview() {
    GPTTheme {
        DailyQuote(Quote("Where there is will there is a way","Hello"))
    }
}