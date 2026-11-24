package com.kova.app.ui.splash

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kova.app.ui.theme.KovaIndigo
import kotlinx.coroutines.delay

/**
 * Splash screen shown on every launch. Presents the Kova logo, tagline and
 * privacy feature bullets; advances to the main app after ~2 seconds or when
 * the user taps Continue.
 */
@Composable
fun SplashScreen(onContinue: () -> Unit) {
    var continued by remember { mutableStateOf(false) }

    fun go() {
        if (!continued) {
            continued = true
            onContinue()
        }
    }

    LaunchedEffect(Unit) {
        delay(2_000)
        go()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 32.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        KovaLogo(logoSize = 112.dp)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Kova",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your thoughts. Yours alone.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.weight(1f))

        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                FeatureRow(
                    icon = Icons.Outlined.CloudOff,
                    text = "Offline first",
                    tint = MaterialTheme.colorScheme.primary
                )
                FeatureRow(
                    icon = Icons.Outlined.Block,
                    text = "No ads",
                    tint = MaterialTheme.colorScheme.primary
                )
                FeatureRow(
                    icon = Icons.Outlined.VisibilityOff,
                    text = "No tracking",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { go() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp)
        ) {
            Text(
                text = "Continue",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun FeatureRow(
    icon: ImageVector,
    text: String,
    tint: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.size(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * The Kova logo: a rounded brand-color square tile with a stylized white "K"
 * drawn with Canvas strokes. Reused in the navigation drawer header.
 */
@Composable
fun KovaLogo(logoSize: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(logoSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val dimension = size.minDimension
            val corner = dimension * 0.24f
            drawRoundRect(
                color = KovaIndigo,
                cornerRadius = CornerRadius(corner, corner)
            )
            val stroke = dimension * 0.075f
            val cap = StrokeCap.Round
            val stemX = dimension * 0.34f
            drawLine(
                color = Color.White,
                start = Offset(stemX, dimension * 0.26f),
                end = Offset(stemX, dimension * 0.74f),
                strokeWidth = stroke,
                cap = cap
            )
            drawLine(
                color = Color.White,
                start = Offset(stemX, dimension * 0.5f),
                end = Offset(dimension * 0.64f, dimension * 0.28f),
                strokeWidth = stroke,
                cap = cap
            )
            drawLine(
                color = Color.White,
                start = Offset(stemX, dimension * 0.5f),
                end = Offset(dimension * 0.64f, dimension * 0.72f),
                strokeWidth = stroke,
                cap = cap
            )
        }
    }
}
