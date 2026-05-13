package com.hastakala.shop.data.dao

import androidx.room.*
import com.hastakala.shop.data.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY name")
    fun getAll(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE stock <= lowStockThreshold")
    fun getLowStock(): Flow<List<Product>>

    @Query("SELECT COUNT(*) FROM products")
    fun totalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM products WHERE stock <= lowStockThreshold")
    fun lowStockCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM products WHERE stock = 0")
    fun outOfStockCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<Product>)

    @Query("UPDATE products SET stock = stock - :qty WHERE id = :id")
    suspend fun decrementStock(id: String, qty: Int = 1)
}

@Dao
interface ColorDao {
    @Query("SELECT * FROM color_variants WHERE productId = :productId ORDER BY colorName")
    fun forProduct(productId: String): Flow<List<ColorVariant>>

    @Query("SELECT * FROM color_variants ORDER BY productId, colorName")
    fun all(): Flow<List<ColorVariant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(variant: ColorVariant)

    @Update
    suspend fun update(variant: ColorVariant)

    @Delete
    suspend fun delete(variant: ColorVariant)

    @Query("DELETE FROM color_variants WHERE productId = :productId")
    suspend fun deleteForProduct(productId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(colors: List<ColorVariant>)

    @Query("UPDATE color_variants SET stock = stock - :qty WHERE productId = :productId AND colorName = :colorName")
    suspend fun decrementColorStock(productId: String, colorName: String, qty: Int = 1)

    @Query("UPDATE color_variants SET stock = :stock WHERE id = :id")
    suspend fun setStock(id: Long, stock: Int)

    @Query("SELECT IFNULL(SUM(stock),0) FROM color_variants WHERE productId = :productId")
    suspend fun totalStockForProduct(productId: String): Int

    @Query("SELECT COUNT(*) FROM color_variants")
    fun totalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM color_variants WHERE stock = 0")
    fun outOfStockCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM color_variants WHERE stock <= 2 AND stock > 0")
    fun lowStockCount(): Flow<Int>
}

@Dao
interface SaleDao {
    @Query("SELECT * FROM sales ORDER BY timestamp DESC LIMIT :limit")
    fun recent(limit: Int = 20): Flow<List<Sale>>

    @Query("SELECT IFNULL(SUM(price * quantity),0) FROM sales WHERE timestamp >= :since")
    fun totalSince(since: Long): Flow<Double>

    @Query("SELECT COUNT(*) FROM sales WHERE timestamp >= :since")
    fun countSince(since: Long): Flow<Int>

    @Query("SELECT productName as name, COUNT(*) as count, SUM(price * quantity) as total FROM sales GROUP BY productName ORDER BY count DESC")
    fun bestSellers(): Flow<List<ProductSalesAgg>>

    @Query("SELECT color as name, COUNT(*) as count FROM sales GROUP BY color ORDER BY count DESC")
    fun colorPerformance(): Flow<List<ColorAgg>>

    @Insert
    suspend fun insert(sale: Sale): Long
}

data class ProductSalesAgg(val name: String, val count: Int, val total: Double)
data class ColorAgg(val name: String, val count: Int)
