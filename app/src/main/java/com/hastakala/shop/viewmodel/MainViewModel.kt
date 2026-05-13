package com.hastakala.shop.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hastakala.shop.data.HastaKalaDatabase
import com.hastakala.shop.data.entities.ColorVariant
import com.hastakala.shop.data.entities.Product
import com.hastakala.shop.data.entities.Sale
import com.hastakala.shop.data.repository.SalesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = SalesRepository(HastaKalaDatabase.get(app))

    val products = repo.products.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val lowStock = repo.lowStock.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    
    // Stats now based on individual items (color variants)
    val totalProducts = repo.totalItems.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val lowStockCount = repo.lowStockCount.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val outOfStockCount = repo.outOfStockCount.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    
    val recentSales = repo.recentSales.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val bestSellers = repo.bestSellers.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val colorPerformance = repo.colorPerformance.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val allColors = repo.allColors.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val todaySales = repo.salesSince(startOfToday()).stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)
    val yesterdaySales = repo.salesSince(startOfYesterday())
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)
    val weekSales = repo.salesSince(startOfOfWeek()).stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)
    val weekSalesCount = repo.salesCountSince(startOfOfWeek()).stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    fun colorsForProduct(productId: String) = repo.colorsForProduct(productId)

    fun addSale(sale: Sale) = viewModelScope.launch { repo.addSale(sale) }

    fun updateColorStock(id: Long, stock: Int) = viewModelScope.launch { repo.setColorStock(id, stock) }

    // Product CRUD
    fun addProduct(product: Product) = viewModelScope.launch { repo.addProduct(product) }
    fun updateProduct(product: Product) = viewModelScope.launch { repo.updateProduct(product) }
    fun deleteProduct(product: Product) = viewModelScope.launch { repo.deleteProduct(product) }

    // Color Variant CRUD
    fun addColorVariant(variant: ColorVariant) = viewModelScope.launch { repo.addColorVariant(variant) }
    fun updateColorVariant(variant: ColorVariant) = viewModelScope.launch { repo.updateColorVariant(variant) }
    fun deleteColorVariant(variant: ColorVariant) = viewModelScope.launch { repo.deleteColorVariant(variant) }

    private fun startOfToday(): Long {
        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0); c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }
    private fun startOfYesterday(): Long = startOfToday() - 24 * 3600_000L
    private fun startOfOfWeek(): Long = startOfToday() - 6 * 24 * 3600_000L
}
