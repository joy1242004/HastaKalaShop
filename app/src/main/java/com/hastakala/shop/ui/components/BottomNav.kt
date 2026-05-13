package com.hastakala.shop.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hastakala.shop.ui.navigation.Routes

private data class NavItem(val route: String, val label: String, val icon: ImageVector)

@Composable
fun BottomNav(current: String, onSelect: (String) -> Unit) {
    val items = listOf(
        NavItem(Routes.DASHBOARD, "Home", Icons.Outlined.Home),
        NavItem(Routes.ADD_SALE, "Add", Icons.Outlined.AddCircle),
        NavItem(Routes.HISTORY, "History", Icons.Outlined.History),
        NavItem(Routes.ANALYTICS, "Stats", Icons.Outlined.QueryStats),
        NavItem(Routes.STOCK, "Stock", Icons.Outlined.Inventory2),
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        windowInsets = NavigationBarDefaults.windowInsets
    ) {
        items.forEach { item ->
            val selected = current == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onSelect(item.route) },
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        item.label,
                        fontSize = 12.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                ),
                alwaysShowLabel = true
            )
        }
    }
}
