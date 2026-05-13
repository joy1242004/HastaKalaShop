package com.hastakala.shop.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.hastakala.shop.data.entities.ColorVariant
import com.hastakala.shop.data.entities.Product
import com.hastakala.shop.ui.theme.*
import com.hastakala.shop.viewmodel.MainViewModel
import java.util.UUID

@Composable
fun StockScreen(vm: MainViewModel) {
    val products by vm.products.collectAsStateWithLifecycle()
    val lowCnt by vm.lowStockCount.collectAsStateWithLifecycle()
    val outOfStock by vm.outOfStockCount.collectAsStateWithLifecycle()
    val total by vm.totalProducts.collectAsStateWithLifecycle()
    val allColors by vm.allColors.collectAsStateWithLifecycle()

    var editingVariant by remember { mutableStateOf<ColorVariant?>(null) }
    var addingProduct by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                Text("Stock Management", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    "Manage your handcrafted inventory and color-wise stock.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp
                )
                Spacer(Modifier.height(8.dp))
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.weight(1f)) {
                        StockStat("Total", total.toString(), Icons.Outlined.Inventory, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onSurface)
                    }
                    Box(Modifier.weight(1f)) {
                        StockStat("Low", lowCnt.toString(), Icons.Outlined.Warning, MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.error)
                    }
                    Box(Modifier.weight(1f)) {
                        StockStat("Out", outOfStock.toString(), Icons.Outlined.Block, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
            item { Spacer(Modifier.height(4.dp)) }
            items(products, key = { it.id }) { p ->
                val variants = allColors.filter { it.productId == p.id }
                ProductRow(
                    p, variants,
                    onEditVariant = { editingVariant = it },
                    onEditProduct = { editingProduct = p },
                    onDeleteProduct = { productToDelete = p }
                )
            }
            item { Spacer(Modifier.height(80.dp)) }
        }

        FloatingActionButton(
            onClick = { addingProduct = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Default.Add, "Add Product")
        }
    }

    if (addingProduct) {
        AddEditProductDialog(
            onDismiss = { addingProduct = false },
            onSave = { product, variants ->
                vm.addProduct(product)
                variants.forEach { vm.addColorVariant(it) }
                addingProduct = false
            }
        )
    }

    editingProduct?.let { product ->
        val variants = allColors.filter { it.productId == product.id }
        AddEditProductDialog(
            product = product,
            initialVariants = variants,
            onDismiss = { editingProduct = null },
            onSave = { updatedProduct, updatedVariants ->
                vm.updateProduct(updatedProduct)
                updatedVariants.forEach { v ->
                    if (v.id == 0L) vm.addColorVariant(v) else vm.updateColorVariant(v)
                }
                editingProduct = null
            }
        )
    }

    editingVariant?.let { variant ->
        EditColorStockDialog(
            variant = variant,
            onDismiss = { editingVariant = null },
            onSave = { newStock -> vm.updateColorStock(variant.id, newStock); editingVariant = null },
        )
    }

    productToDelete?.let { product ->
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Delete Product?") },
            text = { Text("This will permanently remove '${product.name}' and all its color variants.") },
            confirmButton = {
                TextButton(onClick = { vm.deleteProduct(product); productToDelete = null }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
private fun StockStat(
    label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color, iconTint: Color, valueColor: Color
) {
    Card(
        Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = valueColor)
        }
    }
}

@Composable
private fun ProductRow(
    p: Product,
    variants: List<ColorVariant>,
    onEditVariant: (ColorVariant) -> Unit,
    onEditProduct: () -> Unit,
    onDeleteProduct: () -> Unit
) {
    val isLow = p.stock <= p.lowStockThreshold
    val isOut = p.stock == 0
    Card(
        Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isOut) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f) else if (isLow) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (p.imageUri != null) {
                        AsyncImage(
                            model = p.imageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            p.category.take(1).uppercase(), fontSize = 28.sp, fontWeight = FontWeight.Bold,
                            color = if (isOut) MaterialTheme.colorScheme.onSurfaceVariant else if (isLow) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(p.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    if (isOut) {
                        Text("Out of Stock", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    } else if (isLow) {
                        Text("Low Stock: ${p.stock} left", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    } else {
                        Text("${p.stock} in stock", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    }
                    Text("Ref: ${p.ref}  •  ${p.category}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                }
                Row {
                    IconButton(onClick = onEditProduct) {
                        Icon(Icons.Outlined.Edit, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = onDeleteProduct) {
                        Icon(Icons.Outlined.Delete, null, tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
                    }
                }
            }
            if (variants.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Text("Color-wise stock", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    variants.forEach { v ->
                        ColorStockRow(v, onEdit = { onEditVariant(v) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorStockRow(v: ColorVariant, onEdit: () -> Unit) {
    val swatch = runCatching { Color(android.graphics.Color.parseColor(v.colorHex)) }.getOrDefault(MaterialTheme.colorScheme.primary)
    val low = v.stock <= 2
    val out = v.stock == 0
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (v.imageUri != null) {
            AsyncImage(
                model = v.imageUri,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(swatch)
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(v.colorName, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        Text(
            if (out) "OUT" else "${v.stock} pcs",
            color = if (out || low) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold, fontSize = 13.sp,
        )
        Spacer(Modifier.width(10.dp))
        Box(
            Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .clickable { onEdit() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Edit, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(15.dp))
        }
    }
}

@Composable
private fun AddEditProductDialog(
    product: Product? = null,
    initialVariants: List<ColorVariant> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (Product, List<ColorVariant>) -> Unit,
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var category by remember { mutableStateOf(product?.category ?: "") }
    var ref by remember { mutableStateOf(product?.ref ?: "") }
    var price by remember { mutableStateOf(product?.standardPrice?.toString() ?: "") }
    var threshold by remember { mutableStateOf(product?.lowStockThreshold?.toString() ?: "10") }
    var imageUri by remember { mutableStateOf(product?.imageUri) }
    
    val variants = remember { mutableStateListOf<ColorVariant>().apply { addAll(initialVariants) } }

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri?.toString()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (product == null) "Add New Product" else "Edit Product", fontWeight = FontWeight.Bold) },
        confirmButton = {
            Button(
                onClick = {
                    val pId = product?.id ?: UUID.randomUUID().toString()
                    val p = Product(
                        id = pId,
                        name = name,
                        category = category,
                        ref = ref,
                        stock = variants.sumOf { it.stock },
                        standardPrice = price.toDoubleOrNull() ?: 0.0,
                        lowStockThreshold = threshold.toIntOrNull() ?: 10,
                        imageUri = imageUri
                    )
                    onSave(p, variants.map { it.copy(productId = pId) })
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant) } },
        containerColor = MaterialTheme.colorScheme.surface,
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { imageLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Outlined.AddPhotoAlternate, null, tint = MaterialTheme.colorScheme.primary)
                                Text("Add Product Image", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
                item {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Product Name") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = ref, onValueChange = { ref = it }, label = { Text("Reference ID") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = price,
                            onValueChange = { price = it },
                            label = { Text("Price") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        OutlinedTextField(
                            value = threshold,
                            onValueChange = { threshold = it },
                            label = { Text("Low Stock Alert") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }
                item {
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Text("Color Variants", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                items(variants.size) { index ->
                    val v = variants[index]
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(runCatching { Color(android.graphics.Color.parseColor(v.colorHex)) }.getOrDefault(MaterialTheme.colorScheme.primary))
                        )
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text(v.colorName, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Text("${v.stock} in stock", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { variants.removeAt(index) }) {
                            Icon(Icons.Outlined.Close, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                        }
                    }
                }
                item {
                    TextButton(
                        onClick = { variants.add(ColorVariant(productId = "", colorName = "New Color", colorHex = "#E2725B", stock = 0)) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, null)
                        Text("Add Color Variant")
                    }
                }
            }
        }
    )
}

@Composable
private fun EditColorStockDialog(
    variant: ColorVariant,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit,
) {
    var text by remember(variant.id) { mutableStateOf(variant.stock.toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onSave(text.toIntOrNull()?.coerceAtLeast(0) ?: 0) }) {
                Text("Save", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant) } },
        title = { Text("Update ${variant.colorName} stock", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it.filter { c -> c.isDigit() }.take(5) },
                label = { Text("Stock") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
    )
}
