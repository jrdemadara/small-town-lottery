package com.slicksoftcoder.smalltownlottery.features.userprofile

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.slicksoftcoder.smalltownlottery.R
import com.slicksoftcoder.smalltownlottery.features.dashboard.DashboardActivity
import com.slicksoftcoder.smalltownlottery.server.ApiInterface
import com.slicksoftcoder.smalltownlottery.server.LocalDatabase
import com.slicksoftcoder.smalltownlottery.server.NodeServer
import com.slicksoftcoder.smalltownlottery.util.NetworkChecker
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class UserProfileActivity : AppCompatActivity() {
    private lateinit var localDatabase: LocalDatabase
    private lateinit var networkChecker: NetworkChecker
    private lateinit var cardViewUpdatePassword: CardView
    private lateinit var cardViewEnableFingerPrint: CardView
    private lateinit var switchEnableFingerprint: Switch
    private lateinit var buttonClose: FloatingActionButton
    private var onlineStatus by Delegates.notNull<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        localDatabase = LocalDatabase(this)
        cardViewUpdatePassword = findViewById(R.id.cardViewUpdatePassword)
        cardViewEnableFingerPrint = findViewById(R.id.cardViewEnableFingerprint)
        switchEnableFingerprint = findViewById(R.id.switchEnableFingerprint)
        buttonClose = findViewById(R.id.floatingButtonProfileBack)
        checkNetworkConnection()
        val isEnabled: Boolean = localDatabase.checkBiometric()
        switchEnableFingerprint.isChecked = isEnabled
        switchEnableFingerprint.setOnClickListener {
            if (switchEnableFingerprint.isChecked) {
                switchEnableFingerprint.text = "Disable"
                localDatabase.enableBiometric(1)
            } else {
                switchEnableFingerprint.text = "Enable"
                localDatabase.enableBiometric(0)
            }
        }

        cardViewUpdatePassword.setOnClickListener {
            val dialog = Dialog(this)
            val view = layoutInflater.inflate(R.layout.change_password_dialog, null)
            dialog.setCancelable(true)
            dialog.window?.attributes?.windowAnimations = R.style.BottomDialogAnimation
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setGravity(Gravity.BOTTOM)
            dialog.setContentView(view)
            val editTextOldPassword: EditText = view.findViewById(R.id.editTextOldPassword)
            val editTextNewPassword: EditText = view.findViewById(R.id.editTextNewPassword)
            val editTextConfirmPassword: EditText = view.findViewById(R.id.editTextConfirmPassword)
            val buttonUpdate: Button = view.findViewById(R.id.buttonPasswordConfirm)
            if (onlineStatus == 1) {
                dialog.show()
                buttonUpdate.setOnClickListener {
                    val oldPassword = localDatabase.retrievePassword()
                    val agent = localDatabase.retrieveAgent()
                    if (editTextOldPassword.text.toString() !== "" && editTextNewPassword.text.toString() !== "" && editTextConfirmPassword.text.toString() !== "") {
                        if (editTextOldPassword.text.toString() == oldPassword) {
                            if (editTextNewPassword.text.toString() == editTextConfirmPassword.text.toString()) {
                                updatePassword(editTextConfirmPassword.text.toString(), agent, dialog)
                            } else {
                                Toast.makeText(applicationContext, "Confirm password don't match.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(applicationContext, "Old password don't match.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(applicationContext, "Please complete the required fields.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(applicationContext, "Please connect to internet and try again.", Toast.LENGTH_SHORT).show()
            }
        }

        buttonClose.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun checkNetworkConnection() {
        networkChecker = NetworkChecker(application)
        networkChecker.observe(this) { isConnected ->
            onlineStatus = if (isConnected) {
                1
            } else {
                0
            }
        }
    }

    private fun updatePassword(password: String, agent: String, dialog: Dialog) {
        val retIn = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
        retIn.updatePassword(password, agent).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    Toast.makeText(applicationContext, "Password has been updated.", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(applicationContext, "Something went wrong, Please contact developer.", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(applicationContext, "Please check your internet connection.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
