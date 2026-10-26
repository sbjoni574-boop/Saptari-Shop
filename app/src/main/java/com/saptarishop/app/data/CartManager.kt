package com.saptarishop.app.data

import android.content.Context
import android.content.SharedPreferences
import com.saptarishop.app.model.CartItem
import org.json.JSONArray
import org.json.JSONObject

/**
 * डिभाइसमा स्थानीय रूपमा कार्ट डेटा राख्ने व्यवस्थापक। हल्का JSON serialization प्रयोग गरिएको छ
 * (बाह्य dependency नथप्न) ताकि एप सरल र हल्का रहोस्।
 */
object CartManager {

    private const val PREFS_NAME = "saptari_shop_cart"
    private const val KEY_CART_ITEMS = "cart_items_json"

    private lateinit var prefs: SharedPreferences
    private val listeners = mutableListOf<() -> Unit>()

    fun init(context: Context) {
        if (!::prefs.isInitialized) {
            prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    fun addListener(listener: () -> Unit) {
        listeners.add(listener)
    }

    fun removeListener(listener: () -> Unit) {
        listeners.remove(listener)
    }

    private fun notifyChanged() {
        listeners.forEach { it() }
    }

    fun getItems(): MutableList<CartItem> {
        val json = prefs.getString(KEY_CART_ITEMS, null) ?: return mutableListOf()
        return try {
            val arr = JSONArray(json)
            val list = mutableListOf<CartItem>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    CartItem(
                        productId = obj.optString("productId"),
                        name = obj.optString("name"),
                        imageUrl = obj.optString("imageUrl"),
                        price = obj.optDouble("price"),
                        unit = obj.optString("unit"),
                        quantity = obj.optInt("quantity", 1),
                        stock = obj.optInt("stock", 0)
                    )
                )
            }
            list
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    private fun saveItems(items: List<CartItem>) {
        val arr = JSONArray()
        items.forEach { item ->
            val obj = JSONObject()
            obj.put("productId", item.productId)
            obj.put("name", item.name)
            obj.put("imageUrl", item.imageUrl)
            obj.put("price", item.price)
            obj.put("unit", item.unit)
            obj.put("quantity", item.quantity)
            obj.put("stock", item.stock)
            arr.put(obj)
        }
        prefs.edit().putString(KEY_CART_ITEMS, arr.toString()).apply()
        notifyChanged()
    }

    fun addItem(item: CartItem) {
        val items = getItems()
        val existing = items.find { it.productId == item.productId }
        if (existing != null) {
            val newQty = (existing.quantity + item.quantity).coerceAtMost(
                if (item.stock > 0) item.stock else Int.MAX_VALUE
            )
            existing.quantity = newQty
        } else {
            items.add(item)
        }
        saveItems(items)
    }

    fun updateQuantity(productId: String, quantity: Int) {
        val items = getItems()
        val item = items.find { it.productId == productId } ?: return
        if (quantity <= 0) {
            items.remove(item)
        } else {
            item.quantity = if (item.stock > 0) quantity.coerceAtMost(item.stock) else quantity
        }
        saveItems(items)
    }

    fun removeItem(productId: String) {
        val items = getItems()
        items.removeAll { it.productId == productId }
        saveItems(items)
    }

    fun clear() {
        prefs.edit().remove(KEY_CART_ITEMS).apply()
        notifyChanged()
    }

    fun itemCount(): Int = getItems().sumOf { it.quantity }

    fun subtotal(): Double = getItems().sumOf { it.lineTotal }
}
