package com.slicksoftcoder.smalltownlottery.features.landing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.slicksoftcoder.smalltownlottery.R
import com.slicksoftcoder.smalltownlottery.features.authenticate.AuthenticateActivity

class LandingActivity : AppCompatActivity() {
    private lateinit var buttonGetStarted: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonGetStarted = findViewById(R.id.buttonGetStarted)
        buttonGetStarted.setOnClickListener {
            val intent = Intent(this, AuthenticateActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}