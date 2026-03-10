package com.example.icalculator.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.icalculator.ui.theme.DisplayWhite
import com.example.icalculator.ui.theme.ExprGray
import com.example.icalculator.ui.theme.OrangeBtn

@Composable
fun DisplayArea(
    display: String,
    expression: String,
    cursorPosition: Int = display.length,
    onTapCharacter: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val targetSize = when {
        display.length > 9 -> 46f
        display.length > 7 -> 62f
        display.length > 5 -> 78f
        else -> 96f
    }

    val fontSize by animateFloatAsState(
        targetValue = targetSize,
        animationSpec = spring(
            Spring.DampingRatioLowBouncy,
            Spring.StiffnessMediumLow
        ),
        label = "font"
    )

    // Blinking cursor animation
    val cursorAlpha by rememberInfiniteTransition(label = "cursor")
        .animateFloat(
            initialValue = 1f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                tween(530, easing = LinearEasing),
                RepeatMode.Reverse
            ),
            label = "blink"
        )

    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val density = LocalDensity.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Expression line
        AnimatedVisibility(
            visible = expression.isNotEmpty(),
            enter = fadeIn(tween(180)) + expandVertically(),
            exit  = fadeOut(tween(120)) + shrinkVertically()
        ) {
            Text(
                text = expression,
                fontSize = 18.sp,
                fontWeight = FontWeight.Light,
                color = ExprGray,
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp)
            )
        }

        // Main display number + cursor
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(display) {
                    detectTapGestures { tapOffset ->
                        textLayoutResult?.let { layout ->
                            val tappedPos = layout.getOffsetForPosition(tapOffset)
                            onTapCharacter(tappedPos)
                        }
                    }
                }
        ) {
            // The number text
            Text(
                text = display,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Thin,
                color = DisplayWhite,
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
                onTextLayout = { result ->
                    textLayoutResult = result
                }
            )

            // Blinking orange cursor line
            val safePos = cursorPosition.coerceIn(0, display.length)
            val cursorRect = remember(safePos, textLayoutResult) {
                try {
                    textLayoutResult?.getCursorRect(safePos)
                } catch (e: Exception) {
                    null
                }
            }

            if (cursorRect != null && display != "0") {
                Box(
                    modifier = Modifier
                        .offset(
                            x = with(density) { cursorRect.left.toDp() },
                            y = with(density) { cursorRect.top.toDp() }
                        )
                        .width(2.5.dp)
                        .height(with(density) { cursorRect.height.toDp() })
                        .background(OrangeBtn.copy(alpha = cursorAlpha))
                )
            }
        }

        // Helper hint text
        if (display != "0" && !display.contains("Error")) {
            Text(
                text = "✦ tap any digit to move cursor",
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.18f),
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }
    }
}