package com.example.icalculator.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.icalculator.model.HistoryItem
import com.example.icalculator.ui.theme.ExprGray
import com.example.icalculator.ui.theme.OrangeBtn

@Composable
fun HistorySheet(
    history: List<HistoryItem>,
    onDismiss: () -> Unit,
    onClear: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(onClick = onDismiss)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.65f)
                .align(Alignment.TopCenter)
                .padding(top = 60.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1C1C1E), Color(0xFF0A0A0A))
                    )
                )
                .clickable(enabled = false, onClick = {})
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "History",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                    if (history.isNotEmpty()) {
                        Text(
                            text = "Clear",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = OrangeBtn,
                            modifier = Modifier.clickable(onClick = onClear)
                        )
                    }
                }

                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(Color.White.copy(0.1f))
                )

                if (history.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No calculations yet",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Light,
                            color = ExprGray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(history) { item ->
                            HistoryItemRow(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryItemRow(item: HistoryItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(0.05f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = item.expression,
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            color = ExprGray
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "= ${item.result}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Thin,
            color = Color.White
        )
    }
}