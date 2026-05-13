package com.hastakala.shop.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hastakala.shop.data.dao.*
import com.hastakala.shop.data.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(entities = [Product::class, ColorVariant::class, Sale::class], version = 4, exportSchema = false)
abstract class HastaKalaDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun colorDao(): ColorDao
    abstract fun saleDao(): SaleDao

    companion object {
        @Volatile private var INSTANCE: HastaKalaDatabase? = null

        fun get(context: Context): HastaKalaDatabase {
            return INSTANCE ?: synchronized(this) {
                val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    HastaKalaDatabase::class.java, "hastakala.db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(d: SupportSQLiteDatabase) {
                        super.onCreate(d)
                        scope.launch { INSTANCE?.let { Seeder.seed(it) } }
                    }
                }).build()
                INSTANCE = db
                db
            }
        }
    }
}

object Seeder {
    suspend fun seed(db: HastaKalaDatabase) {
        val products = listOf(
            Product("p1", "Handmade Bag", "Bags", "WB-001", 24, 1200.0),
            Product("p2", "Terracotta Vase", "Pottery", "TV-001", 15, 850.0),
            Product("p3", "Woven Bag", "Bags", "WB-004", 2, 1400.0, lowStockThreshold = 5),
            Product("p4", "Silk Scarf", "Textiles", "SS-010", 42, 950.0),
            Product("p5", "Ceramic Mug", "Pottery", "CM-102", 5, 320.0, lowStockThreshold = 8),
            Product("p6", "Sage Keychain", "Accessories", "SK-021", 60, 450.0),
            Product("p7", "Indigo Bag", "Bags", "IB-031", 18, 1200.0),
            Product("p8", "Terracotta Pot", "Pottery", "TP-040", 12, 850.0),
            Product("p9", "Hand-Woven Ikat Runner", "Textiles", "IR-050", 8, 2400.0),
            Product("p10", "Majestic Terracotta Urn", "Pottery", "MTU-070", 4, 4500.0, lowStockThreshold = 5),
        )
        db.productDao().insertAll(products)

        val palette = listOf(
            "Terracotta" to "#B8552E",
            "Indigo" to "#4A6B8A",
            "Sage" to "#6B8A5A",
            "Mustard" to "#D4A845",
        )
        val colors = mutableListOf<ColorVariant>()
        products.forEach { p ->
            // Distribute total stock roughly across the four colors
            val per = p.stock / palette.size
            val rem = p.stock - per * palette.size
            palette.forEachIndexed { idx, (name, hex) ->
                val s = per + if (idx < rem) 1 else 0
                colors += ColorVariant(productId = p.id, colorName = name, colorHex = hex, stock = s)
            }
        }
        db.colorDao().insertAll(colors)

        val now = System.currentTimeMillis()
        val day = 24 * 60 * 60 * 1000L
        val sd = db.saleDao()
        listOf(
            Sale(productId = "p1", productName = "Handmade Bag", color = "Terracotta", price = 1200.0, timestamp = now - 2 * 3600_000),
            Sale(productId = "p6", productName = "Sage Keychain", color = "Sage", price = 450.0, timestamp = now - 5 * 3600_000),
            Sale(productId = "p8", productName = "Terracotta Pot", color = "Terracotta", price = 850.0, timestamp = now - 7 * 3600_000),
            Sale(productId = "p10", productName = "Majestic Terracotta Urn", color = "Terracotta", price = 4500.0, timestamp = now - 1 * day),
            Sale(productId = "p9", productName = "Hand-Woven Ikat Runner", color = "Indigo", price = 2400.0, timestamp = now - 2 * day),
            Sale(productId = "p1", productName = "Handmade Bag", color = "Indigo", price = 1200.0, timestamp = now - 3 * day),
            Sale(productId = "p2", productName = "Terracotta Vase", color = "Terracotta", price = 850.0, timestamp = now - 3 * day),
            Sale(productId = "p4", productName = "Silk Scarf", color = "Mustard", price = 950.0, timestamp = now - 4 * day),
            Sale(productId = "p1", productName = "Handmade Bag", color = "Sage", price = 1200.0, timestamp = now - 4 * day),
            Sale(productId = "p1", productName = "Handmade Bag", color = "Mustard", price = 1200.0, timestamp = now - 5 * day),
        ).forEach { sd.insert(it) }
    }
}
