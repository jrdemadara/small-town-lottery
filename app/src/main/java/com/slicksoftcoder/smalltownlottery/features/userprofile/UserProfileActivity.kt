package com.slicksoftcoder.smalltownlottery.features.userprofile

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.slicksoftcoder.smalltownlottery.R
import com.slicksoftcoder.smalltownlottery.features.dashboard.DashboardActivity
import com.slicksoftcoder.smalltownlottery.features.history.HistoryActivity
import com.slicksoftcoder.smalltownlottery.server.LocalDatabase

class UserProfileActivity : AppCompatActivity() {
    private lateinit var localDatabase: LocalDatabase
    private lateinit var cardViewUpdatePassword: CardView
    private lateinit var cardViewEnableFingerPrint: CardView
    private lateinit var switchEnableFingerprint: Switch
    private lateinit var buttonClose: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        localDatabase = LocalDatabase(this)
        cardViewUpdatePassword = findViewById(R.id.cardViewUpdatePassword)
        cardViewEnableFingerPrint = findViewById(R.id.cardViewEnableFingerprint)
        switchEnableFingerprint = findViewById(R.id.switchEnableFingerprint)
        buttonClose = findViewById(R.id.floatingButtonProfileBack)
        val isEnabled: Boolean = localDatabase.checkBiometric()
        switchEnableFingerprint.isChecked = isEnabled
        Toast.makeText(applicationContext, isEnabled.toString(), Toast.LENGTH_SHORT).show()
        switchEnableFingerprint.setOnClickListener {
            if (switchEnableFingerprint.isChecked){
                switchEnableFingerprint.text = "Disable"
                localDatabase.enableBiometric(1)
            }else{
                switchEnableFingerprint.text = "Enable"
                localDatabase.enableBiometric(0)
            }
        }

        cardViewUpdatePassword.setOnClickListener {
            val dialog = Dialog(this)
            val view = layoutInflater.inflate(R.layout.draw_option_dialog, null)
            dialog.setCancelable(false)
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.setContentView(view)
            dialog.show()
        }

        cardViewUpdatePassword.setOnClickListener {

        }

        buttonClose.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}