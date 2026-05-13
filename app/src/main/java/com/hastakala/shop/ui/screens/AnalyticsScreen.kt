package com.hastakala.shop.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hastakala.shop.ui.components.*
import com.hastakala.shop.ui.theme.*
import com.hastakala.shop.viewmodel.MainViewModel

@Composable
fun AnalyticsScreen(vm: MainViewModel) {
    val best by vm.bestSellers.collectAsStateWithLifecycle()
    val colors by vm.colorPerformance.collectAsStateWithLifecycle()
    val recent by vm.recentSales.collectAsStateWithLifecycle()
    var range by remember { mutableStateOf("This Week") }

    val palette = listOf(Terracotta, Indigo, Sage, TerracottaLight, Mustard)
    val totalSold = best.sumOf { it.count }.coerceAtLeast(1)
    val slices = best.take(4).mapIndexed { i, b ->
        DonutSlice(b.name, b.count.toFloat(), palette[i % palette.size])
    }
    val topProduct = best.firstOrNull()?.name ?: "Handmade Bags"

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(8.dp))
        Text("PERFORMANCE HUB", color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Text("Analytics Dashboard", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(14.dp))

        // toggle
        Row(Modifier.clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(4.dp)) {
            listOf("This Week", "This Month").forEach { opt ->
                val sel = range == opt
                Box(
                    Modifier.clip(RoundedCornerShape(10.dp))
                        .background(if (sel) MaterialTheme.colorScheme.surface else Color.Transparent)
                        .clickable { range = opt }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) { Text(opt, fontWeight = if (sel) FontWeight.Bold else FontWeight.Medium) }
            }
        }

        Spacer(Modifier.height(14.dp))
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(0.dp)) {
            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("TOP PRODUCT", color = MaterialTheme.colorScheme.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text(topProduct, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        SectionCard(title = "Product Distribution", subtitle = "Top performing categories") {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                AnimatedDonutChart(
                    slices = slices.ifEmpty {
                        listOf(
                            DonutSlice("Bags", 40f, Terracotta),
                            DonutSlice("Pottery", 30f, Indigo),
                            DonutSlice("Jewelry", 20f, Sage),
                            DonutSlice("Others", 10f, TerracottaLight),
                        )
                    },
                    centerTitle = "${totalSold}",
                    centerSubtitle = "Total Sold",
                )
            }
            Spacer(Modifier.height(14.dp))
            // legend grid
            val legend = (slices.ifEmpty {
                listOf(
                    DonutSlice("Bags", 40f, Terracotta),
                    DonutSlice("Pottery", 30f, Indigo),
                    DonutSlice("Jewelry", 20f, Sage),
                    DonutSlice("Others", 10f, TerracottaLight),
                )
            })
            val total = legend.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(1f)
            legend.chunked(2).forEach { row ->
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    row.forEach { s ->
                        Box(Modifier.weight(1f)) {
                            LegendDot(s.color, "${s.label} (${(s.value / total * 100).toInt()}%)")
                        }
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        SectionCard(title = "Color Performance", subtitle = "Customer visual preferences") {
            val colorMap = mapOf(
                "Terracotta" to Terracotta, "Red" to Terracotta, "Indigo" to Indigo,
                "Blue" to Indigo, "Sage" to Sage, "Green" to Sage,
                "Sand" to Sand, "Mustard" to Mustard, "Charcoal" to Charcoal, "Char" to Charcoal,
            )
            val bars = if (colors.isNotEmpty()) {
                colors.take(5).map { c ->
                    BarItem(c.name.take(5), c.count.toFloat(), colorMap[c.name] ?: Terracotta)
                }
            } else listOf(
                BarItem("Red", 85f, Terracotta), BarItem("Blue", 60f, Indigo),
                BarItem("Sand", 45f, Sand), BarItem("Sage", 70f, Sage), BarItem("Char", 25f, Charcoal),
            )
            AnimatedBarChart(bars, maxLabel = 100)
        }

        Spacer(Modifier.height(14.dp))
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            elevation = CardDefaults.cardElevation(0.dp)) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AutoAwesome, null, tint = MaterialTheme.colorScheme.onPrimary)
                    Spacer(Modifier.width(8.dp))
                    Text("Artisan Tips", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Spacer(Modifier.height(10.dp))
                TipBox("Red items are currently trending! Increase your stock for the upcoming festive weekend.")
                Spacer(Modifier.height(8.dp))
                TipBox("Consider restocking blue bags soon. Engagement for this color has risen by 15% this week.")
            }
        }

        Spacer(Modifier.height(14.dp))
        SectionCard(title = "Recent High-Value Sales", subtitle = null, trailingText = "View All") {
            recent.sortedByDescending { it.price }.take(2).forEach { s ->
                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(56.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.primary)) {}
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(s.productName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${s.color}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("₹${"%,.0f".format(s.price)}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text("recent", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun TipBox(text: String) {
    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
        .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.18f)).padding(12.dp)) {
        Text(text, color = MaterialTheme.colorScheme.onPrimary, fontSize = 13.sp)
    }
}

@Composable
private fun SectionCard(title: String, subtitle: String?, trailingText: String? = null, content: @Composable ColumnScope.() -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(0.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    if (subtitle != null) Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                }
                if (trailingText != null) Text(trailingText, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}
