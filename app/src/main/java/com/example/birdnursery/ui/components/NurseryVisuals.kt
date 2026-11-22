package com.example.birdnursery.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.birdnursery.data.model.hasHatched
import kotlinx.coroutines.delay

@Composable
fun rememberCurrentTime(updateIntervalMillis: Long = 30_000L): Long {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(updateIntervalMillis) {
        while (true) {
            delay(updateIntervalMillis)
            now = System.currentTimeMillis()
        }
    }
    return now
}

@Composable
fun FeederView(level: Float, modifier: Modifier = Modifier) {
    val animatedLevel by animateFloatAsState(targetValue = level, label = "feederLevel")
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val left = w * 0.29f
        val top = h * 0.12f
        val bodyWidth = w * 0.42f
        val bodyHeight = h * 0.64f
        val bottom = top + bodyHeight
        val fillTop = bottom - bodyHeight * animatedLevel
        val outline = Color(0xFF5D4A3A)

        drawRoundRect(
            color = Color(0xFFB0BEC5),
            topLeft = Offset(w * 0.24f, h * 0.06f),
            size = Size(w * 0.52f, h * 0.09f),
            cornerRadius = CornerRadius(h * 0.035f)
        )
        drawRoundRect(
            color = Color(0xFFE8F0F2).copy(alpha = 0.65f),
            topLeft = Offset(left, top),
            size = Size(bodyWidth, bodyHeight),
            cornerRadius = CornerRadius(w * 0.055f)
        )

        if (animatedLevel > 0f) {
            drawRect(
                color = Color(0xFFD6A94E).copy(alpha = 0.35f),
                topLeft = Offset(left + w * 0.025f, fillTop),
                size = Size(bodyWidth - w * 0.05f, bottom - fillTop - h * 0.015f)
            )
        }

        repeat(36) { index ->
            val seedX = left + bodyWidth * (0.10f + ((index * 37) % 78) / 100f)
            val seedY = top + bodyHeight * (0.08f + ((index * 29) % 84) / 100f)
            if (seedY >= fillTop) {
                drawOval(
                    color = if (index % 3 == 0) Color(0xFF8B6338) else Color(0xFFE1B85B),
                    topLeft = Offset(seedX, seedY),
                    size = Size(w * 0.045f, h * 0.023f)
                )
            }
        }

        drawRoundRect(
            color = outline.copy(alpha = 0.65f),
            topLeft = Offset(left, top),
            size = Size(bodyWidth, bodyHeight),
            cornerRadius = CornerRadius(w * 0.055f),
            style = Stroke(w * 0.012f)
        )
        drawRoundRect(
            color = Color(0xFF9B6B43),
            topLeft = Offset(w * 0.16f, h * 0.76f),
            size = Size(w * 0.68f, h * 0.14f),
            cornerRadius = CornerRadius(h * 0.05f)
        )
        drawRoundRect(
            color = Color(0xFF65452F),
            topLeft = Offset(w * 0.12f, h * 0.88f),
            size = Size(w * 0.76f, h * 0.055f),
            cornerRadius = CornerRadius(h * 0.025f)
        )
        drawLine(
            color = outline,
            start = Offset(w * 0.50f, h * 0.01f),
            end = Offset(w * 0.50f, h * 0.065f),
            strokeWidth = w * 0.014f
        )
    }
}

@Composable
fun HatchingEggView(expectedHatchDate: Long, now: Long, modifier: Modifier = Modifier) {
    val hatched = hasHatched(expectedHatchDate, now)
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val nestColor = Color(0xFF8B5E3C)
        val twigColor = Color(0xFF5D4037)

        repeat(7) { index ->
            val y = h * (0.73f + index * 0.027f)
            drawArc(
                color = if (index % 2 == 0) nestColor else twigColor,
                startAngle = 8f,
                sweepAngle = 164f,
                useCenter = false,
                topLeft = Offset(w * (0.17f + index * 0.008f), y - h * 0.15f),
                size = Size(w * (0.66f - index * 0.016f), h * 0.27f),
                style = Stroke(h * 0.025f)
            )
        }

        if (!hatched) {
            drawOval(
                color = Color(0xFFFFF8E8),
                topLeft = Offset(w * 0.35f, h * 0.10f),
                size = Size(w * 0.30f, h * 0.61f)
            )
            drawOval(
                color = twigColor.copy(alpha = 0.65f),
                topLeft = Offset(w * 0.35f, h * 0.10f),
                size = Size(w * 0.30f, h * 0.61f),
                style = Stroke(w * 0.009f)
            )
            drawOval(
                color = Color.White.copy(alpha = 0.65f),
                topLeft = Offset(w * 0.41f, h * 0.17f),
                size = Size(w * 0.065f, h * 0.20f)
            )
            val crack = Path().apply {
                moveTo(w * 0.50f, h * 0.35f)
                lineTo(w * 0.455f, h * 0.405f)
                lineTo(w * 0.52f, h * 0.445f)
                lineTo(w * 0.465f, h * 0.51f)
                lineTo(w * 0.535f, h * 0.55f)
            }
            drawPath(crack, twigColor, style = Stroke(w * 0.012f))
        } else {
            drawCircle(Color(0xFFF5C85B), w * 0.17f, Offset(w * 0.50f, h * 0.56f))
            drawCircle(Color(0xFFFFD978), w * 0.145f, Offset(w * 0.52f, h * 0.32f))
            drawOval(
                color = Color(0xFFE5AC43),
                topLeft = Offset(w * 0.37f, h * 0.45f),
                size = Size(w * 0.16f, h * 0.19f)
            )
            drawCircle(Color(0xFF30261F), w * 0.018f, Offset(w * 0.575f, h * 0.285f))
            drawCircle(Color.White, w * 0.006f, Offset(w * 0.568f, h * 0.275f))
            val beak = Path().apply {
                moveTo(w * 0.65f, h * 0.32f)
                lineTo(w * 0.76f, h * 0.355f)
                lineTo(w * 0.645f, h * 0.385f)
                close()
            }
            drawPath(beak, Color(0xFFE58B3A))
            drawLine(
                Color(0xFFD87932),
                Offset(w * 0.655f, h * 0.355f),
                Offset(w * 0.73f, h * 0.355f),
                w * 0.007f
            )

            val leftShell = Path().apply {
                moveTo(w * 0.20f, h * 0.72f)
                lineTo(w * 0.24f, h * 0.55f)
                lineTo(w * 0.30f, h * 0.62f)
                lineTo(w * 0.34f, h * 0.54f)
                lineTo(w * 0.36f, h * 0.74f)
                close()
            }
            val rightShell = Path().apply {
                moveTo(w * 0.66f, h * 0.73f)
                lineTo(w * 0.69f, h * 0.57f)
                lineTo(w * 0.74f, h * 0.64f)
                lineTo(w * 0.79f, h * 0.56f)
                lineTo(w * 0.82f, h * 0.73f)
                close()
            }
            drawPath(leftShell, Color(0xFFFFF8E8))
            drawPath(leftShell, twigColor.copy(alpha = 0.55f), style = Stroke(w * 0.007f))
            drawPath(rightShell, Color(0xFFFFF8E8))
            drawPath(rightShell, twigColor.copy(alpha = 0.55f), style = Stroke(w * 0.007f))
        }
    }
}
