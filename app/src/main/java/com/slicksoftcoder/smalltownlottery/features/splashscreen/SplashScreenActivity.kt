package com.slicksoftcoder.smalltownlottery.features.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import com.slicksoftcoder.smalltownlottery.R
import com.slicksoftcoder.smalltownlottery.common.model.DrawUpdateModel
import com.slicksoftcoder.smalltownlottery.common.model.QuotaUpdateModel
import com.slicksoftcoder.smalltownlottery.features.landing.LandingActivity
import com.slicksoftcoder.smalltownlottery.server.ApiInterface
import com.slicksoftcoder.smalltownlottery.server.LocalDatabase
import com.slicksoftcoder.smalltownlottery.server.NodeServer
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
    private var prefquota = "BETQUOTA"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        localDatabase = LocalDatabase(this)
        sharedPreferences = getSharedPreferences(prefname, MODE_PRIVATE)
        networkConnection()
        @Suppress("DEPRECATION")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this,LandingActivity ::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }

    private fun networkConnection() {
        networkChecker = NetworkChecker(application)
        networkChecker.observe(this) { isConnected ->
            if (isConnected) {
                localDatabase.truncateDraws()
                updateDraws()
                updateQuota()
                localDatabase.deleteCanceledBet()
            }
        }
    }

    private fun updateDraws(){
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

    private fun updateQuota(){
        val retrofit = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
        retrofit.updateQuota().enqueue(object : Callback<List<QuotaUpdateModel>?> {
            override fun onResponse(
                call: Call<List<QuotaUpdateModel>?>,
                response:  Response<List<QuotaUpdateModel>?>
            ) {
                val list: List<QuotaUpdateModel?>?
                list = response.body()
                assert(list != null)
                if (list != null) {
                    for (x in list) {
                        updateQuota(x.amount)
                    }
                }
            }

            override fun onFailure(call: Call<List<QuotaUpdateModel>?>, t: Throwable) {
            }
        })
    }

    private fun updateQuota(quota: String?) {
        getSharedPreferences(prefname, MODE_PRIVATE)
            .edit()
            .putString(prefquota, quota)
            .apply()
    }
}