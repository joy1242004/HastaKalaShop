package com.hastakala.shop.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object WhatsAppShare {
    fun send(ctx: Context, phoneE164Digits: String, message: String) {
        val phone = phoneE164Digits.filter { it.isDigit() }
        val url = if (phone.isNotBlank())
            "https://wa.me/$phone?text=" + Uri.encode(message)
        else
            "https://wa.me/?text=" + Uri.encode(message)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            ctx.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(ctx, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
        }
    }

    data class BillItem(
        val name: String,
        val color: String,
        val quantity: Int,
        val price: Double
    )

    fun buildMultiItemBill(
        shopName: String,
        customerName: String,
        items: List<BillItem>
    ): String {
        val nf = { v: Double -> "₹${"%,.0f".format(v)}" }
        val totalAmount = items.sumOf { it.price * it.quantity }
        val cust = if (customerName.isBlank()) "Valued Customer" else customerName
        
        return buildString {
            appendLine("*$shopName — Bill Receipt*")
            appendLine()
            appendLine("Hello $cust, thank you for your purchase!")
            appendLine()
            items.forEachIndexed { index, item ->
                appendLine("${index + 1}. *${item.name}*")
                appendLine("   Color: ${item.color} | Qty: ${item.quantity}")
                appendLine("   Price: ${nf(item.price * item.quantity)}")
            }
            appendLine("---------------------------")
            appendLine("• *Grand Total: ${nf(totalAmount)}*")
            appendLine()
            appendLine("We appreciate your support of handcrafted art. ✨")
        }
    }

    fun buildBill(
        shopName: String,
        customerName: String,
        productName: String,
        color: String,
        quantity: Int,
        unitPrice: Double,
    ): String {
        return buildMultiItemBill(
            shopName, customerName, 
            listOf(BillItem(productName, color, quantity, unitPrice))
        )
    }
}
