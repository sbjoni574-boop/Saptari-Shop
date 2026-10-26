package com.saptarishop.app

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase

class SaptariShopApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        // Offline persistence ले सुस्त नेटवर्कमा पनि क्यास गरिएका उत्पादनहरू देखाउन मद्दत गर्छ।
        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        Firebase.firestore.firestoreSettings = settings
    }

    companion object {
        const val ADMIN_EMAILS_DOC = "config/admins" // Firestore doc listing admin UIDs (for reference/UI only; real enforcement in security rules)
    }
}
