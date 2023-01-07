package com.slicksoftcoder.smalltownlottery.features.landing

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.slicksoftcoder.smalltownlottery.BuildConfig
import com.slicksoftcoder.smalltownlottery.R
import com.slicksoftcoder.smalltownlottery.features.authenticate.AuthenticateActivity

class LandingActivity : AppCompatActivity() {
    private lateinit var buttonGetStarted: Button
    private lateinit var textViewVersion: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonGetStarted = findViewById(R.id.buttonGetStarted)
        textViewVersion = findViewById(R.id.textViewVersionMain)
        textViewVersion.text = "v"+BuildConfig.VERSION_NAME
        checkPermissions()
        buttonGetStarted.setOnClickListener {
            val intent = Intent(this, AuthenticateActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private val PERMISSION = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_PRIVILEGED
    )

    private fun checkPermissions() {
        val permission1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
        val permission2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSION, 1)
        } else if (permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSION, 1)
        }
    }
}
