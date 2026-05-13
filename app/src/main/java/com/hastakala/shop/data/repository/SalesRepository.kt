package com.hastakala.shop.data.repository

import com.hastakala.shop.data.HastaKalaDatabase
import com.hastakala.shop.data.entities.Product
import com.hastakala.shop.data.entities.Sale
import com.hastakala.shop.data.entities.ColorVariant

class SalesRepository(private val db: HastaKalaDatabase) {
    val products = db.productDao().getAll()
    val lowStock = db.productDao().getLowStock()
    
    // Stats based on color variants (the actual items)
    val totalItems = db.colorDao().totalCount()
    val lowStockCount = db.colorDao().lowStockCount()
    val outOfStockCount = db.colorDao().outOfStockCount()
    
    val recentSales = db.saleDao().recent(20)
    val bestSellers = db.saleDao().bestSellers()
    val colorPerformance = db.saleDao().colorPerformance()
    val allColors = db.colorDao().all()

    fun salesSince(since: Long) = db.saleDao().totalSince(since)
    fun salesCountSince(since: Long) = db.saleDao().countSince(since)
    fun colorsForProduct(productId: String) = db.colorDao().forProduct(productId)

    suspend fun addSale(sale: Sale) {
        db.saleDao().insert(sale)
        db.productDao().decrementStock(sale.productId, sale.quantity)
        db.colorDao().decrementColorStock(sale.productId, sale.color, sale.quantity)
    }

    suspend fun setColorStock(id: Long, stock: Int) {
        db.colorDao().setStock(id, stock)
    }

    suspend fun addProduct(product: Product) = db.productDao().insert(product)
    suspend fun updateProduct(product: Product) = db.productDao().update(product)
    suspend fun deleteProduct(product: Product) {
        db.colorDao().deleteForProduct(product.id)
        db.productDao().delete(product)
    }

    suspend fun addColorVariant(variant: ColorVariant) = db.colorDao().insert(variant)
    suspend fun updateColorVariant(variant: ColorVariant) = db.colorDao().update(variant)
    suspend fun deleteColorVariant(variant: ColorVariant) = db.colorDao().delete(variant)
}
