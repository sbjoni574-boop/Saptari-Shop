package com.saptarishop.app.utils

object ValidationUtils {

    /** नेपाली मोबाइल नम्बर ढाँचा: १० अंकको, सामान्यतया 97/98 बाट सुरु हुने */
    fun isValidNepaliPhone(phone: String): Boolean {
        val cleaned = phone.trim()
        return cleaned.length == 10 && cleaned.all { it.isDigit() }
    }

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    }

    fun isValidPrice(priceText: String): Boolean {
        val price = priceText.toDoubleOrNull() ?: return false
        return price > 0
    }
}
