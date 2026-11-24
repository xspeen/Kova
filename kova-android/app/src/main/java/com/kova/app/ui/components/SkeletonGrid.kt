package com.kova.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Shimmering placeholder shown while Room delivers its first emission:
 * a fixed two-column grid of six note-card skeletons with staggered heights
 * and a translating highlight band. Content padding matches the real grid.
 */
@Composable
fun SkeletonNoteGrid(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val shimmer by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerProgress"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SkeletonRow(leftHeight = 140.dp, rightHeight = 110.dp, shimmer = shimmer)
        SkeletonRow(leftHeight = 110.dp, rightHeight = 160.dp, shimmer = shimmer)
        SkeletonRow(leftHeight = 160.dp, rightHeight = 140.dp, shimmer = shimmer)
    }
}

@Composable
private fun SkeletonRow(leftHeight: Dp, rightHeight: Dp, shimmer: Float) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        SkeletonBox(height = leftHeight, shimmer = shimmer, modifier = Modifier.weight(1f))
        SkeletonBox(height = rightHeight, shimmer = shimmer, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun SkeletonBox(height: Dp, shimmer: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .drawWithContent {
                drawContent()
                val bandWidth = size.width
                val x0 = size.width * shimmer - bandWidth
                drawRect(
                    brush = Brush.linearGradient(
                        colorStops = arrayOf(
                            0.0f to Color.White.copy(alpha = 0f),
                            0.5f to Color.White.copy(alpha = 0.35f),
                            1.0f to Color.White.copy(alpha = 0f)
                        ),
                        start = Offset(x0, 0f),
                        end = Offset(x0 + bandWidth, size.height)
                    )
                )
            }
    )
}
