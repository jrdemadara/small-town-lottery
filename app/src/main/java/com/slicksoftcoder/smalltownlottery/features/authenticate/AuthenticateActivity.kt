package com.slicksoftcoder.smalltownlottery.features.authenticate

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.biometrics.BiometricPrompt
import android.os.*
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isGone
import com.slicksoftcoder.smalltownlottery.R
import com.slicksoftcoder.smalltownlottery.common.model.UserUpdateModel
import com.slicksoftcoder.smalltownlottery.features.dashboard.DashboardActivity
import com.slicksoftcoder.smalltownlottery.server.ApiInterface
import com.slicksoftcoder.smalltownlottery.server.LocalDatabase
import com.slicksoftcoder.smalltownlottery.server.NodeServer
import com.slicksoftcoder.smalltownlottery.util.LoadingScreen
import com.slicksoftcoder.smalltownlottery.util.NetworkChecker
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap


class AuthenticateActivity : AppCompatActivity() {
    private lateinit var networkChecker: NetworkChecker
    private lateinit var localDatabase: LocalDatabase
    private lateinit var sharedPreferences: SharedPreferences
    private var prefname = "USERPREF"
    private var prefusername = "USERNAME"
    private var prefpassword = "PASSWORD"
    private var prefdeviceid = "DEVICEID"
    private lateinit var buttonLogin: Button
    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var remember: CheckBox
    private lateinit var textViewForgotPassword: TextView
    private lateinit var imageViewBoimetric : ImageView
    private lateinit var device: String
    private var cancellationSignal : CancellationSignal? = null
    private val authenticationCallback : BiometricPrompt.AuthenticationCallback
        get() =
            object : BiometricPrompt.AuthenticationCallback(){
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                    super.onAuthenticationError(errorCode, errString)
                    notifyUser("Authentication Error : $errString")
                }
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                    super.onAuthenticationSucceeded(result)
                    notifyUser("Authentication Succeeded")
                    switchActivity()
                }
            }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authenticate)
        localDatabase = LocalDatabase(this)
        sharedPreferences = getSharedPreferences(prefname, MODE_PRIVATE)
        val prefusername = sharedPreferences.getString(prefusername, null)
        val prefpassword = sharedPreferences.getString(prefpassword, null)
        device = sharedPreferences.getString(prefdeviceid, null).toString()
        buttonLogin = findViewById(R.id.buttonLogin)
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        remember = findViewById(R.id.checkBoxRemember)
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword)
        imageViewBoimetric = findViewById(R.id.imageViewBiometric)
        val deviceId: UUID = UUID.randomUUID()
        if (prefusername.toString().isBlank() || prefpassword.toString().isBlank()) {
            remember.isChecked = false
        } else {
            editTextUsername.setText(prefusername)
            editTextPassword.setText(prefpassword)
            remember.isChecked = true
        }
        buttonLogin.setOnClickListener {
            if (editTextUsername.text.isNotEmpty() && editTextPassword.text.isNotEmpty()){
                if (remember.isChecked) {
                    rememberMe(editTextUsername.text.toString(), editTextPassword.text.toString())
                } else {
                    clear()
                }

                if (device == "null"){
                    rememberDeviceID(deviceId.toString())
                    updateUserDevice(editTextUsername.text.toString(), editTextPassword.text.toString(),deviceId.toString())
                }else{
                    authenticateOnline(editTextUsername.text.toString(), editTextPassword.text.toString(),device)
                }
            }else{
                Toast.makeText(application, "Please fill the username and password.", Toast.LENGTH_SHORT).show()
                vibratePhone()
            }
        }
        checkBiometric()
    }

    private fun checkBiometric(){
        val isEnabled: Boolean = localDatabase.checkBiometric()
        if (isEnabled){
            imageViewBoimetric.isGone = false
            checkBiometricSupport()
            imageViewBoimetric.setOnClickListener {
                val biometricPrompt = BiometricPrompt.Builder(this)
                    .setTitle("Login with Fingerprint")
                    .setSubtitle("Place your finger on your device's \nfingerprint sensor.")
                    .setNegativeButton("Login with Password" , this.mainExecutor, DialogInterface.OnClickListener { _, _ ->
                        notifyUser("Authentication Cancelled")
                    }).build()
                biometricPrompt.authenticate(getCancellationSignal(),mainExecutor, authenticationCallback)
            }
        }else{
            imageViewBoimetric.isGone = true
        }
    }

    private fun rememberMe(username: String?, password: String?) {
        getSharedPreferences(prefname, MODE_PRIVATE)
            .edit()
            .putString(prefusername, username)
            .putString(prefpassword, password)
            .apply()
    }

    private fun rememberDeviceID(deviceid: String?) {
        getSharedPreferences(prefname, MODE_PRIVATE)
            .edit()
            .putString(prefdeviceid, deviceid)
            .apply()
    }



    private fun clear() {
        val editor = sharedPreferences.edit()
        editor.remove(prefusername)
        editor.remove(prefpassword)
        editor.apply()
    }

    private fun authenticateOnline(username: String, password: String, deviceid: String){
        val retIn = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
        val signInInfo = UserModel(username, password, deviceid)
        retIn.signin(signInInfo).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                LoadingScreen.displayLoadingWithText(applicationContext, "Logging in...", false)
                if (response.code() == 200) {
                    rememberMe(editTextUsername.text.toString(), editTextPassword.text.toString())
                    updateUsers()
                    switchActivity()
                } else if (response.code() == 201) {
                    LoadingScreen.hideLoading()
                    vibratePhone()
                    loginFailed()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                authenticateOffline()
            }
        })
    }

    private fun loginFailed(){
        val dialog = Dialog(this)
        val view = layoutInflater.inflate(R.layout.custom_toast_dialog, null)
        dialog.setCancelable(true)
        dialog.window?.attributes?.windowAnimations = R.style.TopDialogAnimation
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.TOP)
        dialog.setContentView(view)
        dialog.show()
        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
        }, 3000)
    }

    private fun authenticateOffline(){
        if (remember.isChecked) {
            rememberMe(editTextUsername.text.toString(), editTextPassword.text.toString())
        } else {
            clear()
        }
        val isExist: Boolean = localDatabase.authenticate(editTextUsername.text.toString(), editTextPassword.text.toString(), device)
        if (isExist){
            switchActivity()
        }else{
           vibratePhone()
            loginFailed()
        }
    }

    private fun updateUserDevice(username: String, password: String, deviceid: String){
        val retrofit = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
        val filter = HashMap<String, String>()
        filter["deviceid"] = deviceid
        filter["username"] = username
        filter["password"] = password
        retrofit.updateUserDevice(filter).enqueue(object : Callback<ResponseBody?> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (response.code() == 200) {
                    authenticateOnline(editTextUsername.text.toString(), editTextPassword.text.toString(), deviceid)
                    updateUsers()
                } else {
                    Toast.makeText(application, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Toast.makeText(application, "Something went wrong with the server response.", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun updateUsers(){
        localDatabase.truncateUsers()
        val retrofit = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
        retrofit.updateUser(editTextUsername.text.toString()).enqueue(object : Callback<List<UserUpdateModel>?> {
            override fun onResponse(
                call: Call<List<UserUpdateModel>?>,
                response: Response<List<UserUpdateModel>?>
            ) {
                val list: List<UserUpdateModel?>?
                list = response.body()
                assert(list != null)
                if (list != null) {
                    for (x in list) {
                        println(x)
                        localDatabase.updateUsers(
                            x.serial,
                            x.agentSerial,
                            x.username,
                            x.password,
                            x.deviceId,
                            x.location
                        )
                    }
                }
            }

            override fun onFailure(call: Call<List<UserUpdateModel>?>, t: Throwable) {
                Toast.makeText(application, "Something went wrong with the server response.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun vibratePhone() {
        val v = (getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(
                VibrationEffect.createOneShot(500,
                VibrationEffect.DEFAULT_AMPLITUDE))
        }
        else {
            v.vibrate(500)
        }
    }

    private fun getCancellationSignal() : CancellationSignal {
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifyUser("Authentication was Cancelled by the user")
        }
        return cancellationSignal as CancellationSignal
    }

    private fun checkBiometricSupport() : Boolean {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (!keyguardManager.isDeviceSecure){
            notifyUser("Fingerprint authentication has not been enabled in settings")
            return false
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED){
            notifyUser("Fingerprint Authentication Permission is not enabled")
            return false
        }
        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)){
            true
        }else true

    }

    private fun notifyUser(message : String){
        Toast.makeText(this, message , Toast.LENGTH_SHORT).show()
    }

    private fun switchActivity(){
        val intent = Intent( this,DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}