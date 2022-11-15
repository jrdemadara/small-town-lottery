package com.slicksoftcoder.smalltownlottery.features.authenticate

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.*
import android.provider.Settings
import android.telephony.TelephonyManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.slicksoftcoder.smalltownlottery.R
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


class AuthenticateActivity : AppCompatActivity() {
    private lateinit var networkChecker: NetworkChecker
    private lateinit var localDatabase: LocalDatabase
    private lateinit var buttonLogin: Button
    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var textViewUserInitial: TextView
    private lateinit var textViewForgotPassword: TextView
    private lateinit var imageViewBoimetric : ImageView
    private lateinit var deviceId: String
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
        buttonLogin = findViewById(R.id.buttonLogin)
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        textViewUserInitial = findViewById(R.id.textViewUserInitial)
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword)
        imageViewBoimetric = findViewById(R.id.imageViewBiometric)
        deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        //checkNetworkConnection()
        checkBiometricSupport()

//        imageViewBoimetric.setOnClickListener {
//            val intent = Intent(this, DashboardActivity::class.java)
//            startActivity(intent)
//            finish()
//            val biometricPrompt = BiometricPrompt.Builder(this)
//                .setTitle("Login with Fingerprint")
//                .setSubtitle("Place your finger on your device's \nfingerprint sensor.")
//                .setNegativeButton("Login with Password" , this.mainExecutor, DialogInterface.OnClickListener { _, _ ->
//                    notifyUser("Authentication Cancelled")
//                }).build()
//            biometricPrompt.authenticate(getCancellationSignal(),mainExecutor, authenticationCallback)
//        }
    }

    private fun checkNetworkConnection() {
        networkChecker = NetworkChecker(application)
        networkChecker.observe(this) { isConnected ->
            if (isConnected) {
                //* Login Online
                authenticateOnline()
            } else {
                //* Login Online
            }
        }
    }

    private fun authenticateOnline(username: String, password: String, deviceid: String){
        val retIn = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
        val signInInfo = UserModel(username, password, deviceid)
        retIn.signin(signInInfo).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@AuthenticateActivity, t.message, Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                LoadingScreen.displayLoadingWithText(this@AuthenticateActivity, "Logging in...", false)
                if (response.code() == 200) {
                    switchActivity()
                } else if (response.code() == 201) {
                    LoadingScreen.hideLoading()
                    vibratePhone()
                }
            }
        })
    }

    private fun checkDeviceUser(){
        val isExist: Boolean = localDatabase.checkDeviceUser()
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