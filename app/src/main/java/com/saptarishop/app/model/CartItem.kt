package com.saptarishop.app.model

/**
 * कार्ट आइटम — डिभाइसमा मात्र (SharedPreferences/JSON) राखिन्छ, Firestore मा होइन।
 */
data class CartItem(
    var productId: String = "",
    var name: String = "",
    var imageUrl: String = "",
    var price: Double = 0.0,
    var unit: String = "",
    var quantity: Int = 1,
    var stock: Int = 0
) {
    val lineTotal: Double
        get() = price * quantity
}
