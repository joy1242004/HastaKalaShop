package com.hastakala.shop.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hastakala.shop.data.entities.ColorVariant
import com.hastakala.shop.data.entities.Sale
import com.hastakala.shop.ui.theme.*
import com.hastakala.shop.util.UserPrefs
import com.hastakala.shop.util.WhatsAppShare
import kotlinx.coroutines.delay

private data class BillDraftItem(
    val productId: String,
    val productName: String,
    val color: String,
    val qty: Int,
    val price: Double
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuickBillScreen(vm: com.hastakala.shop.viewmodel.MainViewModel) {
    val ctx = LocalContext.current
    val products by vm.products.collectAsStateWithLifecycle()
    val recent by vm.recentSales.collectAsStateWithLifecycle()
    
    var selectedProductId by remember { mutableStateOf<String?>(null) }
    var menuOpen by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf("Terracotta") }
    var price by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("1") }
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var savedFlash by remember { mutableStateOf(false) }

    val billItems = remember { mutableStateListOf<BillDraftItem>() }

    val selected = products.find { it.id == selectedProductId } ?: products.firstOrNull()
    LaunchedEffect(selected) {
        if (selected != null && price.isBlank()) price = selected.standardPrice.toInt().toString()
    }

    val productColors by (selected?.let { vm.colorsForProduct(it.id) }
        ?: kotlinx.coroutines.flow.flowOf(emptyList<ColorVariant>()))
        .collectAsStateWithLifecycle(initialValue = emptyList())

    LaunchedEffect(productColors) {
        if (productColors.isNotEmpty() && productColors.none { it.colorName == selectedColor }) {
            selectedColor = productColors.first().colorName
        }
    }
    val selectedVariant = productColors.find { it.colorName == selectedColor }
    val availableForColor = selectedVariant?.stock ?: 0

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(8.dp))
        Text("Quick Bill Entry", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        Text("Fast tracking for your latest masterpiece sale.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        Spacer(Modifier.height(20.dp))

        // Multi-item Cart Section
        if (billItems.isNotEmpty()) {
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Current Bill Items", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(8.dp))
                    billItems.forEachIndexed { index, item ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(item.productName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("${item.color} • ${item.qty} pcs", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text("₹${"%,.0f".format(item.price * item.qty)}", fontWeight = FontWeight.Bold)
                            IconButton(onClick = { billItems.removeAt(index) }) {
                                Icon(Icons.Outlined.RemoveCircleOutline, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                            }
                        }
                        if (index < billItems.size - 1) HorizontalDivider(Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    }
                    HorizontalDivider(Modifier.padding(vertical = 8.dp), thickness = 1.dp, color = MaterialTheme.colorScheme.primary)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Amount", fontWeight = FontWeight.Bold)
                        Text("₹${"%,.0f".format(billItems.sumOf { it.price * it.qty })}", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        Text("Select Product", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(Modifier.height(8.dp))
        ExposedDropdownMenuBox(expanded = menuOpen, onExpandedChange = { menuOpen = !menuOpen }) {
            OutlinedTextField(
                value = selected?.name ?: "Select",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { Icon(Icons.Outlined.KeyboardArrowDown, null) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                ),
            )
            ExposedDropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                products.forEach { p ->
                    DropdownMenuItem(text = { Text(p.name) }, onClick = {
                        selectedProductId = p.id
                        price = p.standardPrice.toInt().toString()
                        menuOpen = false
                    })
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("Select Color", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(Modifier.height(8.dp))
        val fallback = listOf(
            ColorVariant(productId = "", colorName = "Terracotta", colorHex = "#B8552E", stock = 0),
            ColorVariant(productId = "", colorName = "Indigo", colorHex = "#4A6B8A", stock = 0),
            ColorVariant(productId = "", colorName = "Sage", colorHex = "#6B8A5A", stock = 0),
            ColorVariant(productId = "", colorName = "Mustard", colorHex = "#D4A845", stock = 0),
        )
        val list = if (productColors.isNotEmpty()) productColors else fallback
        FlowRowSimple(items = list) { v ->
            ColorChip(v, selected = selectedColor == v.colorName) { selectedColor = v.colorName }
        }
        Text(
            "Available stock for $selectedColor: $availableForColor",
            color = if (availableForColor <= 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 6.dp),
            fontWeight = FontWeight.Medium,
        )

        Spacer(Modifier.height(20.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(Modifier.weight(2f)) {
                Text("Sale Price", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(8.dp))
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(0.dp)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("₹", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(12.dp))
                        BasicTextFieldStyled(price, { price = it.filter { ch -> ch.isDigit() } })
                    }
                }
            }
            Column(Modifier.weight(1f)) {
                Text("Qty", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(8.dp))
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(0.dp)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        BasicTextFieldStyled(qty, { qty = it.filter { ch -> ch.isDigit() }.take(3) })
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        OutlinedButton(
            onClick = {
                val p = selected ?: return@OutlinedButton
                val amt = price.toDoubleOrNull() ?: return@OutlinedButton
                val q = qty.toIntOrNull()?.coerceAtLeast(1) ?: 1
                billItems.add(BillDraftItem(p.id, p.name, selectedColor, q, amt))
                // Reset for next item
                qty = "1"
            },
            enabled = (selected != null) && availableForColor >= (qty.toIntOrNull() ?: 1),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Outlined.AddShoppingCart, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Add to Bill")
        }

        Spacer(Modifier.height(24.dp))
        Text("Customer (optional)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = customerName, onValueChange = { customerName = it },
            placeholder = { Text("Customer name") },
            leadingIcon = { Icon(Icons.Outlined.Person, null, tint = MaterialTheme.colorScheme.primary) },
            singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant, focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedBorderColor = Color.Transparent, focusedBorderColor = MaterialTheme.colorScheme.primary,
            ),
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = customerPhone,
            onValueChange = { customerPhone = it.filter { c -> c.isDigit() || c == '+' }.take(15) },
            placeholder = { Text("Phone (with country code, e.g. 91XXXXXXXXXX)") },
            leadingIcon = { Icon(Icons.Outlined.Phone, null, tint = MaterialTheme.colorScheme.primary) },
            singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant, focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedBorderColor = Color.Transparent, focusedBorderColor = MaterialTheme.colorScheme.primary,
            ),
        )

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                // If there's an item in the input fields but cart is empty, add it automatically
                if (billItems.isEmpty() && selected != null) {
                    val amt = price.toDoubleOrNull() ?: 0.0
                    val q = qty.toIntOrNull() ?: 1
                    billItems.add(BillDraftItem(selected.id, selected.name, selectedColor, q, amt))
                }
                
                billItems.forEach { item ->
                    vm.addSale(Sale(
                        productId = item.productId, productName = item.productName, color = item.color,
                        price = item.price, timestamp = System.currentTimeMillis(),
                        quantity = item.qty, customerName = customerName, customerPhone = customerPhone,
                    ))
                }
                billItems.clear()
                savedFlash = true
            },
            enabled = billItems.isNotEmpty() || (selected != null && availableForColor >= (qty.toIntOrNull() ?: 1)),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
        ) {
            Icon(Icons.Outlined.Save, null); Spacer(Modifier.width(8.dp))
            val label = if (billItems.size > 1) "Save All (${billItems.size} Items)" else "Save Sale"
            Text(label, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(10.dp))
        OutlinedButton(
            onClick = {
                if (billItems.isEmpty() && selected != null) {
                    val amt = price.toDoubleOrNull() ?: 0.0
                    val q = qty.toIntOrNull() ?: 1
                    billItems.add(BillDraftItem(selected.id, selected.name, selectedColor, q, amt))
                }

                val itemsForBill = billItems.map { 
                    WhatsAppShare.BillItem(it.productName, it.color, it.qty, it.price)
                }

                billItems.forEach { item ->
                    vm.addSale(Sale(
                        productId = item.productId, productName = item.productName, color = item.color,
                        price = item.price, timestamp = System.currentTimeMillis(),
                        quantity = item.qty, customerName = customerName, customerPhone = customerPhone,
                    ))
                }

                val bill = WhatsAppShare.buildMultiItemBill(
                    shopName = "Hasta-Kala Shop",
                    customerName = customerName,
                    items = itemsForBill
                )
                WhatsAppShare.send(ctx, customerPhone, bill)
                billItems.clear()
                savedFlash = true
            },
            enabled = billItems.isNotEmpty() || selected != null,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary),
        ) {
            Icon(Icons.Outlined.Share, null); Spacer(Modifier.width(8.dp))
            Text("Save & Share Multi-Item Bill", fontWeight = FontWeight.Bold)
        }

        AnimatedVisibility(savedFlash, enter = fadeIn(), exit = fadeOut()) {
            LaunchedEffect(Unit) { delay(1500); savedFlash = false }
            Box(Modifier.fillMaxWidth().padding(top = 10.dp).clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer).padding(12.dp)) {
                Text("✓ Sales saved successfully!", color = MaterialTheme.colorScheme.onSecondaryContainer, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(28.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Recent Sales", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.weight(1f))
            Text("View History", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Spacer(Modifier.height(10.dp))
        recent.take(5).forEach { s ->
            RecentSaleRow(s.productName, s.color, "₹${"%,.0f".format(s.price * s.quantity)}")
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun ColorChip(v: ColorVariant, selected: Boolean, onClick: () -> Unit) {
    val color = runCatching { Color(android.graphics.Color.parseColor(v.colorHex)) }.getOrDefault(MaterialTheme.colorScheme.primary)
    val border = if (selected) BorderStroke(2.dp, color) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    val out = if (v.stock <= 0) 0.45f else 1f
    Row(
        Modifier
            .clip(RoundedCornerShape(20.dp))
            .border(border, RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(14.dp).clip(CircleShape).background(color.copy(alpha = out)))
        Spacer(Modifier.width(8.dp))
        Text(v.colorName, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = out))
        Spacer(Modifier.width(6.dp))
        Text("(${v.stock})", fontSize = 11.sp, color = if (v.stock <= 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun RecentSaleRow(name: String, color: String, amount: String) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(0.dp)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold)
                Text(color, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
            Text(amount, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun BasicTextFieldStyled(value: String, onChange: (String) -> Unit) {
    BasicTextField(
        value = value,
        onValueChange = onChange,
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 22.sp, fontWeight = FontWeight.Bold),
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> FlowRowSimple(items: List<T>, content: @Composable (T) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { content(it) }
    }
}
