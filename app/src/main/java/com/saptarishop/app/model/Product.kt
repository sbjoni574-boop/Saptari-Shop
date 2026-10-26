package com.saptarishop.app.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * उत्पादन डेटा मोडेल — Firestore collection: "products"
 */
data class Product(
    @get:Exclude @set:Exclude var id: String = "",
    var name: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var oldPrice: Double = 0.0,
    var unit: String = "पीस",
    var categoryId: String = "",
    var categoryName: String = "",
    var imageUrl: String = "",
    var stock: Int = 0,
    @get:PropertyName("isActive") @set:PropertyName("isActive")
    var isActive: Boolean = true,
    @get:PropertyName("isFeatured") @set:PropertyName("isFeatured")
    var isFeatured: Boolean = false,
    @ServerTimestamp
    var createdAt: Date? = null,
    @ServerTimestamp
    var updatedAt: Date? = null
) {
    @get:Exclude
    val hasDiscount: Boolean
        get() = oldPrice > price

    @get:Exclude
    val discountPercent: Int
        get() = if (hasDiscount && oldPrice > 0) (((oldPrice - price) / oldPrice) * 100).toInt() else 0

    @get:Exclude
    val isInStock: Boolean
        get() = stock > 0
}
