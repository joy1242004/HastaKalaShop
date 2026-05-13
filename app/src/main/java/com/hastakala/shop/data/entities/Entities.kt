package com.hastakala.shop.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val ref: String,
    val stock: Int,
    val standardPrice: Double,
    val lowStockThreshold: Int = 10,
    val imageUri: String? = null,
)

@Entity(tableName = "color_variants")
data class ColorVariant(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: String,
    val colorName: String,
    val colorHex: String,
    val stock: Int = 0,
    val imageUri: String? = null,
)

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: String,
    val productName: String,
    val color: String,
    val price: Double,
    val timestamp: Long,
    val paymentMethod: String = "Cash",
    val quantity: Int = 1,
    val customerName: String = "",
    val customerPhone: String = "",
)
