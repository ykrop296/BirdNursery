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
import androidx.compose.ui.graphics.drawscope.Stroke
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
