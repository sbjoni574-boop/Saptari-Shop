package com.saptarishop.app.model

/**
 * डेलिभरी ठेगाना — सप्तरी जिल्लाका १८ स्थानीय तहमध्ये कुनै एक, वडा नम्बर र गाउँ/टोल सहित।
 */
data class DeliveryAddress(
    var fullName: String = "",
    var phone: String = "",
    var altPhone: String = "",
    var localLevelId: String = "",
    var localLevelName: String = "",
    var wardNo: Int = 0,
    var villageTole: String = "",
    var landmark: String = "",
    var district: String = "सप्तरी",
    var province: String = "मधेश प्रदेश"
) {
    fun isValid(): Boolean {
        return fullName.isNotBlank() &&
            phone.length == 10 &&
            localLevelId.isNotBlank() &&
            wardNo > 0 &&
            villageTole.isNotBlank()
    }

    fun formatted(): String {
        val sb = StringBuilder()
        sb.append(villageTole)
        sb.append(", वडा नं. $wardNo")
        sb.append(", $localLevelName")
        sb.append(", $district, $province")
        if (landmark.isNotBlank()) {
            sb.append(" (नजिक: $landmark)")
        }
        return sb.toString()
    }
}
