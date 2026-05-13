package com.hastakala.shop.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class DonutSlice(val label: String, val value: Float, val color: Color)

@Composable
fun AnimatedDonutChart(slices: List<DonutSlice>, centerTitle: String, centerSubtitle: String) {
    val total = slices.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(1f)
    val progress = remember { Animatable(0f) }
    LaunchedEffect(slices) { progress.animateTo(1f, tween(900, easing = FastOutSlowInEasing)) }

    Box(Modifier.size(180.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 28f
            val sz = Size(size.width - stroke, size.height - stroke)
            val topLeft = Offset(stroke / 2, stroke / 2)
            var start = -90f
            slices.forEach { s ->
                val sweep = (s.value / total) * 360f * progress.value
                drawArc(
                    color = s.color, startAngle = start, sweepAngle = sweep, useCenter = false,
                    topLeft = topLeft, size = sz, style = Stroke(width = stroke)
                )
                start += sweep
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(centerTitle, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(centerSubtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

data class BarItem(val label: String, val value: Float, val color: Color)

@Composable
fun AnimatedBarChart(bars: List<BarItem>, maxLabel: Int = 100) {
    val maxVal = bars.maxOfOrNull { it.value }?.coerceAtLeast(1f) ?: 1f
    val progress = remember { Animatable(0f) }
    LaunchedEffect(bars) { progress.animateTo(1f, tween(900, easing = FastOutSlowInEasing)) }

    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.height(160.dp).fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
            // y-axis labels column
            Column(Modifier.width(28.dp).fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                listOf(maxLabel, maxLabel*3/4, maxLabel/2, maxLabel/4, 0).forEach {
                    Text(it.toString(), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.width(8.dp))
            bars.forEach { b ->
                Column(
                    Modifier.weight(1f).fillMaxHeight(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val h = (b.value / maxVal) * progress.value
                    Box(
                        Modifier.fillMaxWidth(0.6f).fillMaxHeight(h.coerceAtLeast(0.01f))
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(6.dp))
                            .background(b.color)
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth().padding(start = 36.dp)) {
            bars.forEach { b ->
                Text(b.label, modifier = Modifier.weight(1f), fontSize = 11.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(6.dp))
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}
