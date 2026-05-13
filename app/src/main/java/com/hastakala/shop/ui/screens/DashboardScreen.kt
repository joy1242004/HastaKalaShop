package com.hastakala.shop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hastakala.shop.ui.components.StatCard
import com.hastakala.shop.ui.theme.*
import com.hastakala.shop.viewmodel.MainViewModel

@Composable
fun DashboardScreen(vm: MainViewModel, onAddSale: () -> Unit, onAnalytics: () -> Unit, onHistory: () -> Unit) {
    val today by vm.todaySales.collectAsStateWithLifecycle()
    val yest by vm.yesterdaySales.collectAsStateWithLifecycle()
    val week by vm.weekSales.collectAsStateWithLifecycle()
    val weekCnt by vm.weekSalesCount.collectAsStateWithLifecycle()
    val lowCnt by vm.lowStockCount.collectAsStateWithLifecycle()
    val recent by vm.recentSales.collectAsStateWithLifecycle()
    val best by vm.bestSellers.collectAsStateWithLifecycle()

    val pctChange = if (yest > 0) ((today - yest) / yest * 100).toInt() else 12

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(8.dp))
        Text("Namaste, Aruna", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        Text("Here is how your craft is performing today.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Spacer(Modifier.height(18.dp))

        StatCard(
            "TODAY'S SALES", "₹${"%,.0f".format(today)}",
            "↗ ${pctChange}% from yesterday",
            bg = MaterialTheme.colorScheme.primary, fg = MaterialTheme.colorScheme.onPrimary,
        )
        Spacer(Modifier.height(12.dp))
        StatCard(
            "THIS WEEK", "₹${"%,.0f".format(week)}",
            "💳 $weekCnt sales completed",
            bg = MaterialTheme.colorScheme.surfaceVariant, fg = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(12.dp))
        StatCard(
            "LOW STOCK ITEMS", "$lowCnt", "⚠ Action required soon",
            bg = MaterialTheme.colorScheme.errorContainer, fg = MaterialTheme.colorScheme.onErrorContainer,
        )

        Spacer(Modifier.height(20.dp))

        // Product Insights
        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            elevation = CardDefaults.cardElevation(0.dp),
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Product Insights", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.weight(1f))
                    Text("Last 30 Days", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                }
                Spacer(Modifier.height(12.dp))
                Box(
                    Modifier.fillMaxWidth().height(180.dp)
                        .clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(Modifier.align(Alignment.TopStart).padding(10.dp)
                        .clip(RoundedCornerShape(20.dp)).background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.85f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)) {
                        Text("TOP SELLER", color = MaterialTheme.colorScheme.onSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("👜", fontSize = 80.sp)
                }
                Spacer(Modifier.height(14.dp))
                val topName = best.firstOrNull()?.name ?: "Handmade Bags"
                Text(topName, color = MaterialTheme.colorScheme.primary, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.align(Alignment.CenterHorizontally))
                Text(
                    "Your woven bags are your top sellers this month! They account for 42% of your total revenue.",
                    fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 6.dp),
                )
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Tag("#Leather"); Tag("#Woven")
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Recent Activity", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.weight(1f))
            Text("VIEW ALL", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 11.sp,
                modifier = Modifier.clickable { onHistory() })
        }
        Spacer(Modifier.height(8.dp))
        recent.take(2).forEach { s ->
            ActivityRow(s.productName + " Sold", "Just now • Cash", "₹${"%,.0f".format(s.price)}", MaterialTheme.colorScheme.secondaryContainer)
            Spacer(Modifier.height(8.dp))
        }
        ActivityRow("Weekly Payout Processed", "Yesterday • Bank Transfer", "₹24,500", MaterialTheme.colorScheme.tertiaryContainer)

        Spacer(Modifier.height(20.dp))
        // Quick Access
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(0.dp)) {
            Column(Modifier.padding(14.dp)) {
                Text("QUICK ACCESS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickTile("Analytics", Icons.Filled.TrendingUp, Modifier.weight(1f), onAnalytics)
                    QuickTile("History", Icons.Outlined.History, Modifier.weight(1f), onHistory)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        // FAB-ish add button (in column for simplicity)
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            FloatingActionButton(
                onClick = onAddSale,
                containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
            ) { Icon(Icons.Outlined.Add, null) }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun Tag(text: String) {
    Box(Modifier.clip(RoundedCornerShape(20.dp))
        .background(MaterialTheme.colorScheme.secondaryContainer).padding(horizontal = 12.dp, vertical = 6.dp)) {
        Text(text, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
    }
}

@Composable
private fun ActivityRow(title: String, sub: String, amount: String, iconBg: Color) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(0.dp)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(iconBg),
                contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.ShoppingBag, null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(sub, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
            Text(amount, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun QuickTile(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector,
                     modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(modifier.clickable { onClick() }, shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(0.dp)) {
        Column(Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(4.dp))
            Text(label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
    }
}
