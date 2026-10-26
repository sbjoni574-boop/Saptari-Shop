package com.saptarishop.app.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatters {

    private val indianLocale = Locale("en", "IN")

    /** रु. १,२३,४५६ जस्तो भारतीय/नेपाली अङ्क समूहमा मूल्य देखाउँछ */
    fun formatPrice(amount: Double): String {
        val format = if (amount == Math.floor(amount)) {
            NumberFormat.getIntegerInstance(indianLocale)
        } else {
            NumberFormat.getNumberInstance(indianLocale).apply {
                minimumFractionDigits = 2
                maximumFractionDigits = 2
            }
        }
        return "रु. " + format.format(amount)
    }

    fun formatDate(date: Date?): String {
        if (date == null) return "-"
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH)
        return sdf.format(date)
    }
}
