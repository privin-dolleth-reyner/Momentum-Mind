package com.privin.gpt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.privin.gpt.ui.theme.GPTTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GPTTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val state = viewModel.state.collectAsState()
                    when (val value = state.value) {
                        is MainState.Loaded -> {
                            GoldPrice(
                                price = value.price,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        is MainState.Loading -> {
                            GoldPrice(
                                price = -1.0,
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
fun GoldPrice(price: Double, modifier: Modifier = Modifier) {
    if (price == -1.0){
        Text(
            text = "Gold Price Today is Loading...",
            modifier = modifier
        )
    }else{
        Text(
            text = "Gold Price Today is  ₹${"%.2f".format(price)}",
            modifier = modifier
        )
    }

}

@Preview(showBackground = true)
@Composable
fun GoldPricePreview() {
    GPTTheme {
        GoldPrice(7234.1253)
    }
}