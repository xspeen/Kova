package com.kova.app.ui.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kova.app.R
import com.kova.app.ui.theme.KovaIndigo

/**
 * Landing experience: a full-screen, edge-to-edge swipeable hero pager with
 * three pages (brand, library, privacy), an animated page indicator and a
 * single "Get started" action.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SplashScreen(onContinue: () -> Unit) {
    val pagerState = rememberPagerState(initialPage = 0) { 3 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            when (page) {
                0 -> BrandPage()
                1 -> LibraryPage()
                else -> PrivacyPage()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            PageIndicator(
                currentPage = pagerState.currentPage,
                pageCount = 3
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = KovaIndigo,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(R.string.landing_cta),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun BrandPage() {
    val transition = rememberInfiniteTransition(label = "brandFloat")
    val bob by transition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "brandBob"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1.15f))
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(230.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                KovaIndigo.copy(alpha = 0.22f),
                                Color.Transparent
                            )
                        )
                    )
            )
            KovaLogo(
                logoSize = 140.dp,
                modifier = Modifier.graphicsLayer { translationY = bob }
            )
        }
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = stringResource(R.string.landing_page1_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.tagline),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun LibraryPage() {
    HeroPage(
        illustrationRes = R.drawable.ill_library,
        title = stringResource(R.string.landing_page2_title),
        caption = stringResource(R.string.landing_page2_caption)
    )
}

@Composable
private fun PrivacyPage() {
    HeroPage(
        illustrationRes = R.drawable.ill_privacy,
        title = stringResource(R.string.landing_page3_title),
        caption = stringResource(R.string.landing_page3_caption)
    )
}

@Composable
private fun HeroPage(
    illustrationRes: Int,
    title: String,
    caption: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1.15f))
        Image(
            painter = painterResource(illustrationRes),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        )
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = caption,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 40.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun PageIndicator(currentPage: Int, pageCount: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val width by animateDpAsState(
                targetValue = if (index == currentPage) 24.dp else 8.dp,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "indicatorWidth"
            )
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(width)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (index == currentPage) {
                            KovaIndigo
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                        }
                    )
            )
        }
    }
}

/**
 * The Kova logo: a rounded tile filled with the brand diagonal gradient, a
 * subtle top-left highlight streak, a white "K" drawn with rounded strokes
 * and a small accent dot. Drawn entirely with Canvas.
 */
@Composable
fun KovaLogo(logoSize: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(logoSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val d = size.minDimension
            val corner = d * 0.24f
            val tile = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(0f, 0f, d, d),
                        cornerRadius = CornerRadius(corner, corner)
                    )
                )
            }
            drawPath(
                path = tile,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF7C6CF2),
                        KovaIndigo,
                        Color(0xFF5546C2)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(d, d)
                )
            )
            clipPath(tile) {
                rotate(degrees = -32f, pivot = Offset(d * 0.22f, d * 0.2f)) {
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.18f),
                        topLeft = Offset(d * 0.02f, d * 0.02f),
                        size = Size(d * 0.42f, d * 0.14f),
                        cornerRadius = CornerRadius(d * 0.07f, d * 0.07f)
                    )
                }
            }
            val stroke = d * 0.08f
            val cap = StrokeCap.Round
            val stemX = d * 0.34f
            drawLine(
                color = Color.White,
                start = Offset(stemX, d * 0.26f),
                end = Offset(stemX, d * 0.74f),
                strokeWidth = stroke,
                cap = cap
            )
            drawLine(
                color = Color.White,
                start = Offset(stemX, d * 0.5f),
                end = Offset(d * 0.62f, d * 0.28f),
                strokeWidth = stroke,
                cap = cap
            )
            drawLine(
                color = Color.White,
                start = Offset(stemX, d * 0.5f),
                end = Offset(d * 0.62f, d * 0.72f),
                strokeWidth = stroke,
                cap = cap
            )
            drawCircle(
                color = Color.White,
                radius = d * 0.03f,
                center = Offset(d * 0.67f, d * 0.71f)
            )
        }
    }
}
