package com.saptarishop.app.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

enum class OrderStatus(val key: String) {
    PENDING("pending"),
    CONFIRMED("confirmed"),
    SHIPPED("shipped"),
    DELIVERED("delivered"),
    CANCELLED("cancelled");

    companion object {
        fun fromKey(key: String?): OrderStatus =
            values().find { it.key == key } ?: PENDING
    }
}

enum class PaymentMethod(val key: String) {
    COD("cod")
}

/**
 * ग्राहकको अर्डर — Firestore collection: "orders"
 *
 * सुरक्षा नोट: customerUid चाहिं Firebase Auth anonymous/known UID हो जसले ग्राहकलाई
 * आफ्नै मात्र अर्डर हेर्न/ट्र्याक गर्न दिन्छ। एडमिनले सबै अर्डर हेर्न/अपडेट गर्न सक्छ।
 */
data class Order(
    @get:Exclude @set:Exclude var id: String = "",
    var orderNumber: String = "",
    var customerUid: String = "",
    var items: List<OrderItem> = emptyList(),
    var subtotal: Double = 0.0,
    var deliveryCharge: Double = 0.0,
    var totalAmount: Double = 0.0,

    // Delivery address fields (flattened for easy querying/admin display)
    var customerName: String = "",
    var customerPhone: String = "",
    var customerAltPhone: String = "",
    var localLevelId: String = "",
    var localLevelName: String = "",
    var wardNo: Int = 0,
    var villageTole: String = "",
    var landmark: String = "",
    var district: String = "सप्तरी",
    var province: String = "मधेश प्रदेश",

    var paymentMethod: String = PaymentMethod.COD.key,
    var orderNote: String = "",
    var status: String = OrderStatus.PENDING.key,

    @ServerTimestamp
    var createdAt: Date? = null,
    @ServerTimestamp
    var updatedAt: Date? = null
) {
    @get:Exclude
    val statusEnum: OrderStatus
        get() = OrderStatus.fromKey(status)

    @get:Exclude
    val formattedAddress: String
        get() {
            val sb = StringBuilder()
            sb.append(villageTole)
            sb.append(", वडा नं. $wardNo")
            sb.append(", $localLevelName")
            sb.append(", $district")
            if (landmark.isNotBlank()) sb.append(" (नजिक: $landmark)")
            return sb.toString()
        }
}
