package com.example.icalculator.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.icalculator.model.ButtonType
import com.example.icalculator.ui.components.CalcButton
import com.example.icalculator.ui.components.DisplayArea
import com.example.icalculator.ui.components.HistorySheet
import com.example.icalculator.ui.theme.*
import com.example.icalculator.viewmodel.CalculatorViewModel

@Composable
fun CalculatorScreen(vm: CalculatorViewModel = viewModel()) {
    val state   by vm.state.collectAsStateWithLifecycle()
    val history by vm.history.collectAsStateWithLifecycle()

    val screenW    = LocalConfiguration.current.screenWidthDp.dp
    val btnSpacing = 12.dp
    val hPad       = 16.dp
    val btnSize: Dp = ((screenW - hPad * 2 - btnSpacing * 3) / 4).coerceIn(68.dp, 92.dp)

    var showHistory by remember { mutableStateOf(false) }
    var showMenu    by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AnimatedBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ── TOP BAR ──────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Text(
                            text = "⋮",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Light,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier
                            .background(Color(0xFF1C1C1E))
                            .width(180.dp)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "📋  History",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Light
                                )
                            },
                            onClick = {
                                showMenu = false
                                showHistory = true
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "🗑  Clear History",
                                    color = OrangeBtn,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Light
                                )
                            },
                            onClick = {
                                showMenu = false
                                vm.clearHistory()
                            }
                        )
                    }
                }
                Text(
                    text = "Calculator",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraLight,
                    color = Color.White.copy(0.3f),
                    letterSpacing = 3.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            // ── DISPLAY + BUTTONS ─────────────────────────────────────
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Bottom
            ) {
                DisplayArea(
                    display        = state.display,
                    expression     = state.expression,
                    cursorPosition = state.cursorPosition,
                    onTapCharacter = { vm.setCursor(it) },
                    modifier       = Modifier.padding(bottom = 24.dp)
                )

                // ── Rows 1–4 (normal 4-column rows) ──────────────────
                val topRows = listOf(
                    listOf(
                        Triple("AC",  ButtonType.FUNCTION, false),
                        Triple("+/-", ButtonType.FUNCTION, false),
                        Triple("%",   ButtonType.FUNCTION, false),
                        Triple("÷",   ButtonType.OPERATOR, false)
                    ),
                    listOf(
                        Triple("7", ButtonType.NUMBER, false),
                        Triple("8", ButtonType.NUMBER, false),
                        Triple("9", ButtonType.NUMBER, false),
                        Triple("×", ButtonType.OPERATOR, false)
                    ),
                    listOf(
                        Triple("4", ButtonType.NUMBER, false),
                        Triple("5", ButtonType.NUMBER, false),
                        Triple("6", ButtonType.NUMBER, false),
                        Triple("−", ButtonType.OPERATOR, false)
                    ),
                    listOf(
                        Triple("1", ButtonType.NUMBER, false),
                        Triple("2", ButtonType.NUMBER, false),
                        Triple("3", ButtonType.NUMBER, false),
                        Triple("+", ButtonType.OPERATOR, false)
                    )
                )

                topRows.forEachIndexed { rowIdx, row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = hPad),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        row.forEachIndexed { colIdx, (label, type, _) ->
                            val actualLabel = if (label == "AC") vm.acLabel() else label
                            val isActive = state.operator == label
                                    && state.resetOnNextInput
                                    && !state.justCalculated
                            CalcButton(
                                label      = actualLabel,
                                type       = type,
                                size       = btnSize,
                                fontSize   = (btnSize.value * 0.37f).sp,
                                isWide     = false,
                                isActiveOp = isActive,
                                onClick    = { vm.onButton(actualLabel) }
                            )
                            if (colIdx < row.size - 1)
                                Spacer(Modifier.width(btnSpacing))
                        }
                    }
                    Spacer(Modifier.height(btnSpacing))
                }

                // ── Row 5: 0(wide)  .   ⌫   = ───────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = hPad),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 0 normal size
                    CalcButton(
                        label    = "0",
                        type     = ButtonType.NUMBER,
                        size     = btnSize,
                        fontSize = (btnSize.value * 0.37f).sp,
                        isWide   = false,
                        onClick  = { vm.onButton("0") }
                    )
                    Spacer(Modifier.width(btnSpacing))

                    // . decimal
                    CalcButton(
                        label    = ".",
                        type     = ButtonType.NUMBER,
                        size     = btnSize,
                        fontSize = (btnSize.value * 0.50f).sp,
                        isWide   = false,
                        onClick  = { vm.onButton(".") }
                    )
                    Spacer(Modifier.width(btnSpacing))

                    // ⌫ backspace
                    CalcButton(
                        label    = "⌫",
                        type     = ButtonType.FUNCTION,
                        size     = btnSize,
                        fontSize = (btnSize.value * 0.30f).sp,
                        isWide   = false,
                        onClick  = { vm.onButton("⌫") }
                    )
                    Spacer(Modifier.width(btnSpacing))

                    // = equals
                    CalcButton(
                        label    = "=",
                        type     = ButtonType.EQUALS,
                        size     = btnSize,
                        fontSize = (btnSize.value * 0.37f).sp,
                        isWide   = false,
                        onClick  = { vm.onButton("=") }
                    )
                }

                Spacer(Modifier.height(32.dp))
            }
        }

        // ── History overlay ───────────────────────────────────────────
        if (showHistory) {
            HistorySheet(
                history   = history,
                onDismiss = { showHistory = false },
                onClear   = { vm.clearHistory() }
            )
        }
    }
}

@Composable
private fun AnimatedBackground() {
    val inf = rememberInfiniteTransition(label = "bg")
    val t1 by inf.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Reverse),
        label = "t1"
    )
    val t2 by inf.animateFloat(
        1f, 0f,
        infiniteRepeatable(tween(7000, easing = LinearEasing), RepeatMode.Reverse),
        label = "t2"
    )
    val t3 by inf.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(11000, easing = LinearEasing), RepeatMode.Reverse),
        label = "t3"
    )
    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier.size(360.dp)
                .offset((-70 + t1 * 60).dp, (40 + t2 * 90).dp)
                .blur(120.dp)
                .background(GlowPurple.copy(0.32f), CircleShape)
        )
        Box(
            Modifier.size(280.dp)
                .offset((170 - t2 * 70).dp, (280 + t3 * 60).dp)
                .blur(100.dp)
                .background(GlowBlue.copy(0.24f), CircleShape)
        )
        Box(
            Modifier.size(220.dp)
                .offset((90 + t3 * 50).dp, (530 - t1 * 50).dp)
                .blur(90.dp)
                .background(GlowOrange.copy(0.20f), CircleShape)
        )
    }
}
