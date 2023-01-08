package com.slicksoftcoder.smalltownlottery.features.splashscreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.slicksoftcoder.smalltownlottery.BuildConfig
import com.slicksoftcoder.smalltownlottery.R
import com.slicksoftcoder.smalltownlottery.common.model.* // ktlint-disable no-wildcard-imports
import com.slicksoftcoder.smalltownlottery.features.landing.LandingActivity
import com.slicksoftcoder.smalltownlottery.features.outdated.OutdatedActivity
import com.slicksoftcoder.smalltownlottery.features.timeauto.TimeAutoActivity
import com.slicksoftcoder.smalltownlottery.features.undermaintenance.UnderMaintenanceActivity
import com.slicksoftcoder.smalltownlottery.server.ApiInterface
import com.slicksoftcoder.smalltownlottery.server.LocalDatabase
import com.slicksoftcoder.smalltownlottery.server.NodeServer
import com.slicksoftcoder.smalltownlottery.util.LoadingScreen
import com.slicksoftcoder.smalltownlottery.util.NetworkChecker
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var networkChecker: NetworkChecker
    private lateinit var localDatabase: LocalDatabase
    private lateinit var sharedPreferences: SharedPreferences
    private var prefname = "USERPREF"
    private var prefQuotaRegular = "QUOTAREGULAR"
    private var prefQuotaRambolito = "QUOTARAMBOLITO"
    private var prefVersion = "APPVERSION"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        localDatabase = LocalDatabase(this)
        sharedPreferences = getSharedPreferences(prefname, MODE_PRIVATE)
        @Suppress("DEPRECATION")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        if (isTimeAutomatic(this.applicationContext)) {
            checkState()
        } else {
            val intent = Intent(applicationContext, TimeAutoActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun isTimeAutomatic(c: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Settings.Global.getInt(c.contentResolver, Settings.Global.AUTO_TIME, 0) == 1
        } else {
            Settings.System.getInt(c.contentResolver, Settings.System.AUTO_TIME, 0) == 1
        }
    }

    private fun networkConnection() {
        networkChecker = NetworkChecker(application)
        networkChecker.observe(this) { isConnected ->
            if (isConnected) {
                updateDraws()
                updateQuota()
                updateSoldOut()
                updateLowWin()
                updateVersion()
                localDatabase.deleteCanceledBet()
            }
        }
    }

    private fun updateDraws() {
        val retrofit = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
        retrofit.updateDraws().enqueue(object : Callback<List<DrawUpdateModel>?> {
            override fun onResponse(
                call: Call<List<DrawUpdateModel>?>,
                response: Response<List<DrawUpdateModel>?>
            ) {
                val list: List<DrawUpdateModel?>?
                list = response.body()
                assert(list != null)
                if (list != null) {
                    localDatabase.truncateDraws()
                    for (x in list) {
                        println(x)
                        localDatabase.updateDraws(
                            x.serial,
                            x.drawName,
                            x.drawTime,
                            x.cutoff,
                            x.resume
                        )
                    }
                }
            }

            override fun onFailure(call: Call<List<DrawUpdateModel>?>, t: Throwable) {
                Toast.makeText(applicationContext, t.toString(), Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun updateQuota() {
        val retrofit = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
        retrofit.updateQuota().enqueue(object : Callback<List<QuotaUpdateModel>?> {
            override fun onResponse(
                call: Call<List<QuotaUpdateModel>?>,
                response: Response<List<QuotaUpdateModel>?>
            ) {
                val list: List<QuotaUpdateModel?>?
                list = response.body()
                assert(list != null)
                if (list != null) {
                    for (x in list) {
                        updateQuota(x.regular, x.rambolito)
                    }
                }
            }

            override fun onFailure(call: Call<List<QuotaUpdateModel>?>, t: Throwable) {
            }
        })
    }

    private fun updateSoldOut() {
        val retrofit = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
        retrofit.updateSoldOut().enqueue(object : Callback<List<SoldOutModel>?> {
            override fun onResponse(
                call: Call<List<SoldOutModel>?>,
                response: Response<List<SoldOutModel>?>
            ) {
                val list: List<SoldOutModel?>?
                list = response.body()
                assert(list != null)
                if (list != null) {
                    localDatabase.truncateSoldOut()
                    for (x in list) {
                        localDatabase.updateSoldOut(x.number)
                    }
                }
            }

            override fun onFailure(call: Call<List<SoldOutModel>?>, t: Throwable) {
            }
        })
    }

    private fun updateLowWin() {
        val retrofit = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
        retrofit.updateLowWin().enqueue(object : Callback<List<LowWinModel>?> {
            override fun onResponse(
                call: Call<List<LowWinModel>?>,
                response: Response<List<LowWinModel>?>
            ) {
                val list: List<LowWinModel?>?
                list = response.body()
                assert(list != null)
                if (list != null) {
                    localDatabase.truncateLowWin()
                    for (x in list) {
                        localDatabase.updateLowWin(x.number)
                    }
                }
            }

            override fun onFailure(call: Call<List<LowWinModel>?>, t: Throwable) {
            }
        })
    }

    private fun updateVersion() {
        val retrofit = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
        retrofit.checkVersion().enqueue(object : Callback<List<versionModel>?> {
            override fun onResponse(
                call: Call<List<versionModel>?>,
                response: Response<List<versionModel>?>
            ) {
                val list: List<versionModel?>?
                list = response.body()
                assert(list != null)
                if (list != null) {
                    for (x in list) {
                        updateVersion(x.major, x.minor, x.patch, x.tag)
                    }
                }
            }

            override fun onFailure(call: Call<List<versionModel>?>, t: Throwable) {
            }
        })
    }

    private fun updateQuota(regular: String?, rambolito: String?) {
        getSharedPreferences(prefname, MODE_PRIVATE)
            .edit()
            .putString(prefQuotaRegular, regular)
            .putString(prefQuotaRambolito, rambolito)
            .apply()
    }

    private fun updateVersion(major: String?, minor: String?, patch: String?, tag: String?) {
        val version = "$major.$minor.$patch-$tag"
        getSharedPreferences(prefname, MODE_PRIVATE)
            .edit()
            .putString(prefVersion, version)
            .apply()
    }

    private fun checkState() {
        sharedPreferences = getSharedPreferences(prefname, MODE_PRIVATE)
        val prefVersion = sharedPreferences.getString(prefVersion, null)
        val retIn = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
        retIn.checkState().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                LoadingScreen.displayLoadingWithText(applicationContext, "Logging in...", false)
                if (response.code() == 200) {
                    networkConnection()
                    if (prefVersion == BuildConfig.VERSION_NAME) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            val intent = Intent(applicationContext, LandingActivity::class.java)
                            startActivity(intent)
                            finish()
                        }, 3000)
                    } else {
                        val intent = Intent(applicationContext, OutdatedActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else if (response.code() == 201) {
                    val intent = Intent(applicationContext, UnderMaintenanceActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }
        })
    }
}
