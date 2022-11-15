package com.slicksoftcoder.smalltownlottery.features.dashboard

import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CancellationSignal
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.slicksoftcoder.smalltownlottery.R
import com.slicksoftcoder.smalltownlottery.features.bet.BetActivity
import com.slicksoftcoder.smalltownlottery.features.landing.LandingActivity

class DashboardActivity : AppCompatActivity() {
private lateinit var imageViewBet: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        imageViewBet = findViewById(R.id.imageViewBet)
        imageViewBet.setOnClickListener {
            val intent = Intent(this, BetActivity ::class.java)
            startActivity(intent)
            finish()
        }
    }

}