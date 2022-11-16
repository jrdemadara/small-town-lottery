package com.slicksoftcoder.smalltownlottery.features.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.Toast
import com.slicksoftcoder.smalltownlottery.server.ApiInterface
import com.slicksoftcoder.smalltownlottery.server.NodeServer
import com.slicksoftcoder.smalltownlottery.util.NetworkChecker
import com.slicksoftcoder.smalltownlottery.R
import com.slicksoftcoder.smalltownlottery.common.model.DrawUpdateModel
import com.slicksoftcoder.smalltownlottery.common.model.UserUpdateModel
import com.slicksoftcoder.smalltownlottery.features.landing.LandingActivity
import com.slicksoftcoder.smalltownlottery.server.LocalDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var networkChecker: NetworkChecker
    private lateinit var localDatabase: LocalDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        localDatabase = LocalDatabase(this)
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
                            x.cutoff
                        )
                    }
                }
            }

            override fun onFailure(call: Call<List<DrawUpdateModel>?>, t: Throwable) {
                Toast.makeText(applicationContext, t.toString(), Toast.LENGTH_LONG).show()
            }
        })
    }


}