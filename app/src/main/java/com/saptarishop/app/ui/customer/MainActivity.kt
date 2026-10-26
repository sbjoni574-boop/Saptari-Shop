package com.saptarishop.app.ui.customer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.saptarishop.app.R

/**
 * ग्राहक एपको मुख्य पृष्ठ (होम)।
 *
 * TODO (अर्को विकास चरण): यसलाई BottomNavigationView + Navigation Component सहितको
 * पूर्ण Home/Category/Cart/Orders/Account fragment structure मा विस्तार गर्नुपर्छ।
 * हाल यो एउटा न्यूनतम प्लेसहोल्डर हो ताकि प्रोजेक्ट संरचना र Firebase/Cart/data तह
 * (FirebaseRepository, CartManager, SaptariLocalLevels, मोडेलहरू) compile हुने र
 * भविष्यमा UI माथि निर्माण गर्न सकिने आधार बसाइयोस्।
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_placeholder)

        findViewById<TextView>(R.id.tvPlaceholderTitle).text =
            getString(R.string.home_title)
        findViewById<TextView>(R.id.tvPlaceholderBody).text =
            getString(R.string.home_welcome_banner)
    }
}
