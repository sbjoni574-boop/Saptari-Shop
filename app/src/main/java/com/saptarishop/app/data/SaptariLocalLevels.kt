package com.saptarishop.app.data

/**
 * सप्तरी जिल्लाका सबै १८ स्थानीय तहहरू (९ नगरपालिका + ९ गाउँपालिका) र तिनका वडा संख्या।
 * स्रोत: नेपाल सरकार संघीय मामिला तथा सामान्य प्रशासन मन्त्रालय / जिल्ला समन्वय समिति सप्तरी
 * को सार्वजनिक तथ्याङ्कमा आधारित। कुल स्थानीय तह: १८, कुल वडा: १६४।
 */
data class LocalLevel(
    val id: String,
    val nameNp: String,
    val type: LocalLevelType,
    val wardCount: Int
)

enum class LocalLevelType(val labelNp: String) {
    MUNICIPALITY("नगरपालिका"),
    RURAL_MUNICIPALITY("गाउँपालिका")
}

object SaptariLocalLevels {

    val ALL: List<LocalLevel> = listOf(
        LocalLevel("rajbiraj", "राजविराज नगरपालिका", LocalLevelType.MUNICIPALITY, 16),
        LocalLevel("kanchanrup", "कञ्चनरुप नगरपालिका", LocalLevelType.MUNICIPALITY, 12),
        LocalLevel("dakneshwori", "डाकनेश्वरी नगरपालिका", LocalLevelType.MUNICIPALITY, 10),
        LocalLevel("bodebarsain", "बोदेबरसाइन नगरपालिका", LocalLevelType.MUNICIPALITY, 10),
        LocalLevel("khadak", "खडक नगरपालिका", LocalLevelType.MUNICIPALITY, 11),
        LocalLevel("shambhunath", "शम्भुनाथ नगरपालिका", LocalLevelType.MUNICIPALITY, 12),
        LocalLevel("surunga", "सुरुङ्गा नगरपालिका", LocalLevelType.MUNICIPALITY, 11),
        LocalLevel("hanumannagar_kankalini", "हनुमाननगर कंकालिनी नगरपालिका", LocalLevelType.MUNICIPALITY, 14),
        LocalLevel("saptakoshi", "सप्तकोशी नगरपालिका", LocalLevelType.MUNICIPALITY, 11),
        LocalLevel("agnisair_krishnasawaran", "अग्निसाइर कृष्णासवरन गाउँपालिका", LocalLevelType.RURAL_MUNICIPALITY, 6),
        LocalLevel("chhinnamasta", "छिन्नमस्ता गाउँपालिका", LocalLevelType.RURAL_MUNICIPALITY, 7),
        LocalLevel("mahadeva", "महादेवा गाउँपालिका", LocalLevelType.RURAL_MUNICIPALITY, 6),
        LocalLevel("tirhut", "तिरहुत गाउँपालिका", LocalLevelType.RURAL_MUNICIPALITY, 5),
        LocalLevel("tilathi_koiladi", "तिलाठी कोइलाडी गाउँपालिका", LocalLevelType.RURAL_MUNICIPALITY, 8),
        LocalLevel("rupani", "रुपनी गाउँपालिका", LocalLevelType.RURAL_MUNICIPALITY, 6),
        LocalLevel("rajgadh", "राजगढ गाउँपालिका", LocalLevelType.RURAL_MUNICIPALITY, 6),
        LocalLevel("bishnupur", "बिष्णुपुर गाउँपालिका", LocalLevelType.RURAL_MUNICIPALITY, 7),
        LocalLevel("balan_bihul", "बलान-बिहुल गाउँपालिका", LocalLevelType.RURAL_MUNICIPALITY, 6)
    )

    fun findById(id: String?): LocalLevel? = ALL.find { it.id == id }

    fun wardNumbers(localLevelId: String?): List<Int> {
        val level = findById(localLevelId) ?: return emptyList()
        return (1..level.wardCount).toList()
    }

    fun displayNames(): List<String> = ALL.map { it.nameNp }

    fun findByName(name: String?): LocalLevel? = ALL.find { it.nameNp == name }
}
