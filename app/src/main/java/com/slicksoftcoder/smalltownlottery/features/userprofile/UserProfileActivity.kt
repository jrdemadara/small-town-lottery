package com.slicksoftcoder.smalltownlottery.features.userprofile

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.slicksoftcoder.smalltownlottery.R

class UserProfileActivity : AppCompatActivity() {
    private lateinit var cardViewUpdatePassword: CardView
    private lateinit var cardViewEnableFingerPrint: CardView
    private lateinit var switchEnableFingerprint: Switch
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        cardViewUpdatePassword = findViewById(R.id.cardViewUpdatePassword)
        cardViewEnableFingerPrint = findViewById(R.id.cardViewEnableFingerprint)
        switchEnableFingerprint = findViewById(R.id.switchEnableFingerprint)
        switchEnableFingerprint.setOnClickListener {
            if (switchEnableFingerprint.isChecked){
                Toast.makeText(applicationContext, "Check", Toast.LENGTH_SHORT).show()
//                switchEnableFingerprint.trackTint(ColorStateList(Color.))
//                switchEnableFingerprint.thumbTintList(R.color.yellow_500)
            }else{
                Toast.makeText(applicationContext, "Not", Toast.LENGTH_SHORT).show()
//            switchEnableFingerprint.setTrackResource(R.color.black)
//            switchEnableFingerprint.setThumbResource(R.color.black)
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
    }
}