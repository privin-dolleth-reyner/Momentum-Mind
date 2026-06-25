package com.privin.mm.ui.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.privin.data.models.Quote
import com.privin.mm.ui.theme.BodySans
import com.privin.mm.ui.theme.DisplaySerif
import com.privin.mm.ui.theme.quoteCardBrush
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Off-screen host that renders [request] as a branded card, captures it to a PNG
 * and fires a share chooser — then calls [onComplete]. The card is recorded into
 * a [rememberGraphicsLayer] during the draw pass but never painted to the screen
 * (we record without `drawLayer`), so capture happens invisibly. We wait two
 * frames so the first draw — and therefore the recording — has happened before
 * reading the bitmap.
 */
@Composable
fun QuoteImageShareHost(
    request: Quote?,
    onComplete: () -> Unit,
) {
    if (request == null) return

    val context = LocalContext.current
    val dark = isSystemInDarkTheme()
    val graphicsLayer = rememberGraphicsLayer()

    Box(
        modifier = Modifier
            .size(width = 340.dp, height = 420.dp)
            // Record the content into the layer but skip drawing it to the screen.
            .drawWithContent { graphicsLayer.record { this@drawWithContent.drawContent() } },
    ) {
        ShareableQuoteCard(quote = request, dark = dark)
    }

    LaunchedEffect(request) {
        // Let the draw pass run so the layer has recorded the card.
        withFrameNanosTwice()
        val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
        shareQuoteImage(context, bitmap, request)
        onComplete()
    }
}

private suspend fun withFrameNanosTwice() {
    androidx.compose.runtime.withFrameNanos { }
    androidx.compose.runtime.withFrameNanos { }
}

/** The branded artwork that gets rasterised and shared. */
@Composable
private fun ShareableQuoteCard(quote: Quote, dark: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(quoteCardBrush(dark)),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.material3.Text(
            text = "❝",
            color = Color.White.copy(alpha = 0.12f),
            fontFamily = FontFamily.Serif,
            fontSize = 160.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 4.dp),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            androidx.compose.material3.Text(
                text = quote.quote,
                color = Color.White,
                fontFamily = DisplaySerif,
                fontWeight = FontWeight.Medium,
                fontSize = 26.sp,
                lineHeight = 34.sp,
                textAlign = TextAlign.Center,
            )
            Box(
                modifier = Modifier
                    .padding(top = 20.dp, bottom = 12.dp)
                    .width(40.dp)
                    .height(2.dp)
                    .background(Color.White.copy(alpha = 0.6f)),
            )
            androidx.compose.material3.Text(
                text = quote.author,
                color = Color.White.copy(alpha = 0.92f),
                fontFamily = BodySans,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )
        }

        androidx.compose.material3.Text(
            text = "MOMENTUM MIND",
            color = Color.White.copy(alpha = 0.7f),
            fontFamily = BodySans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            letterSpacing = 2.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
                .alpha(0.9f),
        )
    }
}

private suspend fun shareQuoteImage(context: Context, bitmap: Bitmap, quote: Quote) {
    val uri = withContext(Dispatchers.IO) {
        val dir = File(context.cacheDir, "shared_images").apply { mkdirs() }
        val file = File(dir, "quote_share.png")
        FileOutputStream(file).use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TEXT, "${quote.quote}\n— ${quote.author}")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(
        Intent.createChooser(shareIntent, "Share quote").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        },
    )
}

/** Plain-text share, kept as a lightweight fallback. */
fun shareQuoteText(context: Context, quote: Quote) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "${quote.quote}\n— ${quote.author}")
    }
    context.startActivity(
        Intent.createChooser(intent, "Share quote").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        },
    )
}
