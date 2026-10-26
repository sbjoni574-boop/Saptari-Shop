package com.saptarishop.app.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * उत्पादन श्रेणी — Firestore collection: "categories"
 */
data class Category(
    @get:Exclude @set:Exclude var id: String = "",
    var name: String = "",
    var iconUrl: String = "",
    var sortOrder: Int = 0,
    @ServerTimestamp
    var createdAt: Date? = null
)
