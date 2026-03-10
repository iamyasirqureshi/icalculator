package com.example.icalculator.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.icalculator.model.ButtonType
import com.example.icalculator.ui.theme.*

@Composable
fun CalcButton(
    label: String,
    type: ButtonType,
    size: Dp,
    fontSize: TextUnit,
    isWide: Boolean = false,
    isActiveOp: Boolean = false,
    onClick: () -> Unit
) {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()

    // Liquid spring bounce
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.88f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "scale"
    )

    // Liquid glow pulse on press
    val glowAlpha by animateFloatAsState(
        targetValue = if (pressed) 0.7f else 0.25f,
        animationSpec = tween(80),
        label = "glow"
    )

    val (bg, fg) = when {
        isActiveOp && (type == ButtonType.OPERATOR || type == ButtonType.EQUALS) ->
            OrangeBtnPressed to OrangeBtn
        type == ButtonType.OPERATOR || type == ButtonType.EQUALS ->
            (if (pressed) OrangeBtnPressed else OrangeBtn) to Color.White
        type == ButtonType.FUNCTION ->
            (if (pressed) FuncBtnPressed else FuncBtn) to FuncBtnText
        else ->
            (if (pressed) NumBtnPressed else NumBtn) to Color.White
    }

    val shape = if (isWide) RoundedCornerShape(size / 2) else CircleShape

    val shadowColor = when {
        type == ButtonType.OPERATOR || type == ButtonType.EQUALS -> OrangeBtn.copy(glowAlpha)
        type == ButtonType.FUNCTION -> Color.White.copy(glowAlpha * 0.4f)
        else -> Color.Black.copy(0.6f)
    }

    Box(
        modifier = Modifier
            .width(if (isWide) size * 2 + 12.dp else size)
            .height(size)
            .scale(scale)
            .shadow(
                elevation = if (pressed) 4.dp else 14.dp,
                shape = shape,
                ambientColor = shadowColor,
                spotColor = shadowColor
            )
            .clip(shape)
            // Base color
            .background(bg)
            // Liquid glass top shine
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to Color.White.copy(alpha = 0.18f),
                        0.35f to Color.White.copy(alpha = 0.06f),
                        1.0f to Color.Black.copy(alpha = 0.18f)
                    )
                )
            )
            // Glass border shimmer
            .border(
                width = 0.7.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.35f),
                        Color.White.copy(alpha = 0.05f)
                    )
                ),
                shape = shape
            )
            .clickable(interactionSource = src, indication = null, onClick = onClick),
        contentAlignment = if (isWide) Alignment.CenterStart else Alignment.Center
    ) {
        // Inner liquid ripple shine
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = if (pressed) 0.04f else 0.10f),
                            Color.Transparent
                        )
                    )
                )
        )

        Text(
            text = label,
            fontSize = fontSize,
            fontWeight = FontWeight.Light,
            color = fg,
            modifier = if (isWide) Modifier.padding(start = size * 0.29f) else Modifier
        )
    }
}