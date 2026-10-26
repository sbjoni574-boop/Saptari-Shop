package com.saptarishop.app.ui.common

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.saptarishop.app.R
import com.saptarishop.app.data.CartManager
import com.saptarishop.app.data.FirebaseRepository
import com.saptarishop.app.ui.customer.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * स्प्ल्यास स्क्रिन — एप सुरु हुँदा छोटो समय देखिन्छ, त्यसपछि ग्राहक मूल पृष्ठमा जान्छ।
 * यसले CartManager सुरुवात गर्छ र ग्राहकका लागि anonymous Firebase Auth सुनिश्चित गर्छ
 * (ताकि अर्डर सिर्जना र "मेरा अर्डर" ट्र्याकिङ्ग काम गरोस्)।
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        CartManager.init(applicationContext)

        CoroutineScope(Dispatchers.Main).launch {
            FirebaseRepository.ensureAnonymousAuth()
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }, 900)
        }
    }
}
