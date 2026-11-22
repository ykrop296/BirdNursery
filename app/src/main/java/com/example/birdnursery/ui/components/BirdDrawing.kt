package com.example.birdnursery.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun InteractiveBirdView(
    headColor: Color,
    cheekColor: Color,
    throatColor: Color,
    bodyColor: Color,
    chestColor: Color,
    bellyColor: Color,
    wingColor: Color,
    tailColor: Color,
    legColor: Color,
    beakColor: Color,
    onPartTapped: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { offset ->
                detectBirdPart(offset.x / size.width, offset.y / size.height)?.let(onPartTapped)
            }
        }
    ) {
        drawDetailedBird(
            headColor = headColor,
            cheekColor = cheekColor,
            throatColor = throatColor,
            bodyColor = bodyColor,
            chestColor = chestColor,
            bellyColor = bellyColor,
            wingColor = wingColor,
            tailColor = tailColor,
            legColor = legColor,
            beakColor = beakColor,
            isLarge = true
        )
    }
}

fun detectBirdPart(x: Float, y: Float): String? {
    if (x !in 0f..1f || y !in 0f..1f) return null
    return when {
        x in 0.79f..0.96f && y in 0.22f..0.36f -> "beak"
        x in 0.65f..0.80f && y in 0.25f..0.38f -> "cheeks"
        x in 0.57f..0.78f && y in 0.36f..0.49f -> "throat"
        x in 0.52f..0.83f && y in 0.07f..0.38f -> "head"
        x in 0.02f..0.31f && y in 0.52f..0.91f -> "tail"
        x in 0.24f..0.56f && y in 0.36f..0.68f -> "wing"
        x in 0.54f..0.79f && y in 0.43f..0.61f -> "chest"
        x in 0.38f..0.74f && y in 0.59f..0.78f -> "belly"
        x in 0.20f..0.58f && y in 0.25f..0.49f -> "body"
        x in 0.34f..0.72f && y in 0.73f..0.91f -> "legs"
        else -> null
    }
}

fun DrawScope.drawDetailedBird(
    headColor: Color,
    cheekColor: Color,
    throatColor: Color,
    bodyColor: Color,
    chestColor: Color,
    bellyColor: Color,
    wingColor: Color,
    tailColor: Color,
    legColor: Color,
    beakColor: Color,
    isLarge: Boolean = false
) {
    val w = size.width
    val h = size.height
    val unit = minOf(w, h)
    val outline = Color(0xFF40342C)
    val stroke = Stroke(unit * if (isLarge) 0.009f else 0.012f)
    val fineStroke = Stroke(unit * 0.006f)

    val branchY = h * 0.82f
    val branch = Path().apply {
        moveTo(w * 0.12f, branchY)
        cubicTo(w * 0.36f, branchY - h * 0.025f, w * 0.68f, branchY + h * 0.02f, w * 0.94f, branchY - h * 0.015f)
        lineTo(w * 0.95f, branchY + h * 0.035f)
        cubicTo(w * 0.67f, branchY + h * 0.065f, w * 0.35f, branchY + h * 0.025f, w * 0.12f, branchY + h * 0.05f)
        close()
    }
    drawPath(branch, Color(0xFF76523A))
    drawPath(branch, outline, style = fineStroke)

    if (isLarge) {
        val leaf = Path().apply {
            moveTo(w * 0.84f, branchY)
            cubicTo(w * 0.81f, branchY - h * 0.08f, w * 0.90f, branchY - h * 0.12f, w * 0.91f, branchY - h * 0.11f)
            cubicTo(w * 0.94f, branchY - h * 0.04f, w * 0.89f, branchY + h * 0.01f, w * 0.84f, branchY)
            close()
        }
        drawPath(leaf, Color(0xFF789262))
        drawPath(leaf, outline.copy(alpha = 0.65f), style = fineStroke)
        drawLine(
            Color(0xFF526F43),
            Offset(w * 0.84f, branchY),
            Offset(w * 0.90f, branchY - h * 0.095f),
            unit * 0.004f
        )
    }

    val tailBack = Path().apply {
        moveTo(w * 0.29f, h * 0.53f)
        cubicTo(w * 0.20f, h * 0.61f, w * 0.09f, h * 0.76f, w * 0.035f, h * 0.88f)
        cubicTo(w * 0.08f, h * 0.89f, w * 0.17f, h * 0.79f, w * 0.25f, h * 0.69f)
        cubicTo(w * 0.31f, h * 0.61f, w * 0.35f, h * 0.57f, w * 0.36f, h * 0.55f)
        close()
    }
    drawPath(tailBack, tailColor.copy(alpha = 0.82f))
    drawPath(tailBack, outline, style = stroke)

    val tailFront = Path().apply {
        moveTo(w * 0.34f, h * 0.55f)
        cubicTo(w * 0.27f, h * 0.65f, w * 0.18f, h * 0.79f, w * 0.12f, h * 0.91f)
        cubicTo(w * 0.18f, h * 0.89f, w * 0.28f, h * 0.78f, w * 0.36f, h * 0.64f)
        close()
    }
    drawPath(tailFront, tailColor)
    drawPath(tailFront, outline, style = stroke)
    if (isLarge) {
        drawLine(
            tailColor.copy(alpha = 0.55f),
            Offset(w * 0.29f, h * 0.62f),
            Offset(w * 0.145f, h * 0.855f),
            unit * 0.008f
        )
    }

    val body = Path().apply {
        moveTo(w * 0.55f, h * 0.275f)
        cubicTo(w * 0.40f, h * 0.24f, w * 0.26f, h * 0.32f, w * 0.205f, h * 0.48f)
        cubicTo(w * 0.15f, h * 0.64f, w * 0.29f, h * 0.745f, w * 0.47f, h * 0.765f)
        cubicTo(w * 0.64f, h * 0.79f, w * 0.755f, h * 0.70f, w * 0.775f, h * 0.55f)
        cubicTo(w * 0.80f, h * 0.41f, w * 0.71f, h * 0.29f, w * 0.55f, h * 0.275f)
        close()
    }
    drawPath(body, bodyColor)
    drawPath(body, outline, style = stroke)

    val belly = Path().apply {
        moveTo(w * 0.40f, h * 0.59f)
        cubicTo(w * 0.51f, h * 0.54f, w * 0.66f, h * 0.535f, w * 0.765f, h * 0.51f)
        cubicTo(w * 0.79f, h * 0.65f, w * 0.72f, h * 0.73f, w * 0.60f, h * 0.755f)
        cubicTo(w * 0.47f, h * 0.78f, w * 0.37f, h * 0.71f, w * 0.40f, h * 0.59f)
        close()
    }
    drawPath(belly, bellyColor)
    drawPath(belly, outline.copy(alpha = 0.45f), style = fineStroke)

    val chest = Path().apply {
        moveTo(w * 0.565f, h * 0.355f)
        cubicTo(w * 0.68f, h * 0.34f, w * 0.77f, h * 0.39f, w * 0.775f, h * 0.52f)
        cubicTo(w * 0.70f, h * 0.57f, w * 0.58f, h * 0.555f, w * 0.48f, h * 0.51f)
        cubicTo(w * 0.47f, h * 0.43f, w * 0.50f, h * 0.375f, w * 0.565f, h * 0.355f)
        close()
    }
    drawPath(chest, chestColor)
    drawPath(chest, outline.copy(alpha = 0.45f), style = fineStroke)

    val wing = Path().apply {
        moveTo(w * 0.46f, h * 0.355f)
        cubicTo(w * 0.34f, h * 0.34f, w * 0.24f, h * 0.43f, w * 0.22f, h * 0.57f)
        cubicTo(w * 0.20f, h * 0.65f, w * 0.27f, h * 0.69f, w * 0.36f, h * 0.65f)
        cubicTo(w * 0.46f, h * 0.61f, w * 0.535f, h * 0.53f, w * 0.55f, h * 0.45f)
        cubicTo(w * 0.555f, h * 0.40f, w * 0.515f, h * 0.36f, w * 0.46f, h * 0.355f)
        close()
    }
    drawPath(wing, wingColor)
    drawPath(wing, outline, style = stroke)

    val wingHighlight = Path().apply {
        moveTo(w * 0.28f, h * 0.48f)
        cubicTo(w * 0.34f, h * 0.39f, w * 0.44f, h * 0.37f, w * 0.50f, h * 0.40f)
    }
    drawPath(wingHighlight, Color.White.copy(alpha = 0.18f), style = Stroke(unit * 0.018f))

    if (isLarge) {
        listOf(
            Triple(0.47f, 0.45f, 0.27f),
            Triple(0.48f, 0.50f, 0.32f),
            Triple(0.45f, 0.55f, 0.37f)
        ).forEach { (startX, startY, endX) ->
            val feather = Path().apply {
                moveTo(w * startX, h * startY)
                quadraticTo(w * (startX - 0.08f), h * (startY + 0.08f), w * endX, h * 0.635f)
            }
            drawPath(feather, outline.copy(alpha = 0.38f), style = fineStroke)
        }
    }

    val head = Path().apply {
        moveTo(w * 0.535f, h * 0.285f)
        cubicTo(w * 0.52f, h * 0.18f, w * 0.565f, h * 0.10f, w * 0.655f, h * 0.075f)
        cubicTo(w * 0.75f, h * 0.05f, w * 0.825f, h * 0.125f, w * 0.825f, h * 0.225f)
        cubicTo(w * 0.83f, h * 0.315f, w * 0.765f, h * 0.385f, w * 0.675f, h * 0.39f)
        cubicTo(w * 0.595f, h * 0.395f, w * 0.545f, h * 0.35f, w * 0.535f, h * 0.285f)
        close()
    }
    drawPath(head, headColor)
    drawPath(head, outline, style = stroke)

    val headHighlight = Path().apply {
        moveTo(w * 0.575f, h * 0.15f)
        cubicTo(w * 0.62f, h * 0.10f, w * 0.69f, h * 0.09f, w * 0.735f, h * 0.115f)
    }
    drawPath(headHighlight, Color.White.copy(alpha = 0.20f), style = Stroke(unit * 0.018f))

    val cheek = Path().apply {
        moveTo(w * 0.665f, h * 0.275f)
        cubicTo(w * 0.69f, h * 0.235f, w * 0.76f, h * 0.245f, w * 0.785f, h * 0.285f)
        cubicTo(w * 0.81f, h * 0.335f, w * 0.755f, h * 0.37f, w * 0.69f, h * 0.35f)
        cubicTo(w * 0.655f, h * 0.335f, w * 0.645f, h * 0.30f, w * 0.665f, h * 0.275f)
        close()
    }
    drawPath(cheek, cheekColor)
    drawPath(cheek, outline.copy(alpha = 0.35f), style = fineStroke)

    val throat = Path().apply {
        moveTo(w * 0.585f, h * 0.35f)
        cubicTo(w * 0.635f, h * 0.375f, w * 0.70f, h * 0.38f, w * 0.755f, h * 0.355f)
        cubicTo(w * 0.765f, h * 0.41f, w * 0.745f, h * 0.47f, w * 0.68f, h * 0.48f)
        cubicTo(w * 0.615f, h * 0.475f, w * 0.58f, h * 0.415f, w * 0.585f, h * 0.35f)
        close()
    }
    drawPath(throat, throatColor)
    drawPath(throat, outline.copy(alpha = 0.5f), style = fineStroke)

    val eye = Offset(w * 0.744f, h * 0.218f)
    drawCircle(Color(0xFF5A4638), unit * 0.040f, eye)
    drawCircle(Color(0xFF171513), unit * 0.029f, eye)
    drawCircle(Color.White, unit * 0.010f, Offset(eye.x - unit * 0.009f, eye.y - unit * 0.010f))
    if (isLarge) {
        drawCircle(Color.White.copy(alpha = 0.65f), unit * 0.004f, Offset(eye.x + unit * 0.012f, eye.y + unit * 0.012f))
    }

    val upperBeak = Path().apply {
        moveTo(w * 0.805f, h * 0.235f)
        cubicTo(w * 0.855f, h * 0.24f, w * 0.905f, h * 0.265f, w * 0.945f, h * 0.285f)
        lineTo(w * 0.805f, h * 0.305f)
        close()
    }
    drawPath(upperBeak, beakColor)
    drawPath(upperBeak, outline, style = fineStroke)

    val lowerBeak = Path().apply {
        moveTo(w * 0.805f, h * 0.305f)
        lineTo(w * 0.925f, h * 0.295f)
        cubicTo(w * 0.875f, h * 0.335f, w * 0.825f, h * 0.35f, w * 0.785f, h * 0.345f)
        close()
    }
    drawPath(lowerBeak, beakColor.copy(alpha = 0.82f))
    drawPath(lowerBeak, outline, style = fineStroke)
    drawLine(outline.copy(alpha = 0.6f), Offset(w * 0.81f, h * 0.302f), Offset(w * 0.925f, h * 0.292f), unit * 0.004f)

    listOf(0.46f, 0.60f).forEachIndexed { index, legX ->
        val top = Offset(w * legX, h * 0.735f)
        val ankle = Offset(w * (legX - if (index == 0) 0.015f else 0.005f), branchY + h * 0.008f)
        drawLine(legColor, top, ankle, unit * 0.012f)
        drawArc(
            color = legColor,
            startAngle = 175f,
            sweepAngle = 185f,
            useCenter = false,
            topLeft = Offset(ankle.x - w * 0.035f, branchY - h * 0.005f),
            size = Size(w * 0.075f, h * 0.04f),
            style = Stroke(unit * 0.009f)
        )
        drawLine(
            legColor,
            ankle,
            Offset(ankle.x + w * 0.035f, branchY + h * 0.035f),
            unit * 0.007f
        )
    }
}
