package com.saptarishop.app.model

/**
 * अर्डर भित्र रहने प्रत्येक उत्पादनको स्न्यापसट (मूल्य परिवर्तन भए पनि अर्डर इतिहास अपरिवर्तित रहोस्)
 */
data class OrderItem(
    var productId: String = "",
    var name: String = "",
    var imageUrl: String = "",
    var price: Double = 0.0,
    var unit: String = "",
    var quantity: Int = 1
) {
    val lineTotal: Double
        get() = price * quantity
}
