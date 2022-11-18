package com.slicksoftcoder.smalltownlottery.features.dashboard

import android.content.Intent
import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.slicksoftcoder.smalltownlottery.R
import com.slicksoftcoder.smalltownlottery.features.bet.BetActivity
import com.slicksoftcoder.smalltownlottery.features.history.HistoryActivity
import com.slicksoftcoder.smalltownlottery.features.result.ResultActivity
import com.slicksoftcoder.smalltownlottery.server.ApiInterface
import com.slicksoftcoder.smalltownlottery.server.LocalDatabase
import com.slicksoftcoder.smalltownlottery.server.NodeServer
import com.slicksoftcoder.smalltownlottery.util.DateUtil
import com.slicksoftcoder.smalltownlottery.util.NetworkChecker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class DashboardActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var networkChecker: NetworkChecker
    private lateinit var localDatabase: LocalDatabase
    private lateinit var dateUtil: DateUtil
    private lateinit var textViewDate: TextView
    private lateinit var textViewTotalBet: TextView
    private lateinit var textViewTotalHits: TextView
    private lateinit var textViewPNL: TextView
    private lateinit var textViewMenuBet: TextView
    private lateinit var textViewMenuHistory: TextView
    private lateinit var textViewMenuResult: TextView
    private lateinit var textViewMenu: TextView
    private lateinit var cardView2pm: CardView
    private lateinit var cardView5pm: CardView
    private lateinit var cardView9pm: CardView
    private lateinit var textView2pmWinner: TextView
    private lateinit var textView5pmWinner: TextView
    private lateinit var textView9pmWinner: TextView
    private lateinit var textView2pmResult: TextView
    private lateinit var textView5pmResult: TextView
    private lateinit var textView9pmResult: TextView
    private lateinit var textView2pmTotalWin: TextView
    private lateinit var textView5pmTotalWin: TextView
    private lateinit var textView9pmTotalWin: TextView
    private lateinit var imageViewBet: ImageView
    val formatter: NumberFormat = DecimalFormat("#,###")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        localDatabase = LocalDatabase(this)
        bottomNavigationView = findViewById(R.id.bottomNavigationDash)
        toolbar = findViewById(R.id.materialToolbarDash)
        dateUtil = DateUtil()
        textViewDate = findViewById(R.id.textViewDashDate)
        textViewTotalBet = findViewById(R.id.textViewDashTotalBet)
        textViewTotalHits = findViewById(R.id.textViewDashTotalHits)
        textViewPNL = findViewById(R.id.textViewDashPNL)
        textViewMenuBet = findViewById(R.id.textViewDashMenuBet)
        textViewMenuHistory = findViewById(R.id.textViewDashMenuHistory)
        textViewMenuResult = findViewById(R.id.textViewDashMenuResult)
        textViewMenu = findViewById(R.id.textViewDashMenu)
        cardView2pm = findViewById(R.id.cardView2pm)
        cardView5pm = findViewById(R.id.cardView5pm)
        cardView9pm = findViewById(R.id.cardView9pm)
        textView2pmWinner = findViewById(R.id.textViewDash2pmWinner)
        textView5pmWinner = findViewById(R.id.textViewDash5pmWinner)
        textView9pmWinner = findViewById(R.id.textViewDash9pmWinner)
        textView2pmResult = findViewById(R.id.textViewDash2pmResult)
        textView5pmResult = findViewById(R.id.textViewDash5pmResult)
        textView9pmResult = findViewById(R.id.textViewDash9pmResult)
        textView2pmTotalWin = findViewById(R.id.textViewDash2pmTotalWin)
        textView5pmTotalWin = findViewById(R.id.textViewDash5pmTotalWin)
        textView9pmTotalWin = findViewById(R.id.textViewDash9pmTotalWin)
        imageViewBet = findViewById(R.id.imageViewDashBet)
        textViewDate.text = dateUtil.currentDateShort().replace("-", " ").uppercase(Locale.ROOT)

        middleNavigation()
        bottomNavigation()
        retrievePNL()
        retrieveDraw2()
        retrieveDraw5()
        retrieveDraw9()

    }

    private fun bottomNavigation(){
        bottomNavigationView.setOnItemSelectedListener  {
            when (it.itemId) {
                R.id.home -> {
                    val intent = Intent(this, DashboardActivity ::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.history -> {
                    val intent = Intent(this, HistoryActivity ::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.bet -> {
                    val intent = Intent(this, BetActivity ::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.result -> {
                    val intent = Intent(this, ResultActivity ::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.settings -> {
                    val intent = Intent(this, DashboardActivity ::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> {
                    true
                }
            }
        }
    }

    private fun middleNavigation(){
        textViewMenuBet.setOnClickListener {
            val intent = Intent(this, BetActivity ::class.java)
            startActivity(intent)
            finish()
        }

        textViewMenuHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity ::class.java)
            startActivity(intent)
            finish()
        }

        textViewMenuResult.setOnClickListener {
            val intent = Intent(this, ResultActivity ::class.java)
            startActivity(intent)
            finish()
        }
        imageViewBet.setOnClickListener {
            val intent = Intent(this, BetActivity ::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun retrievePNL() {
        val retrofit = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
        retrofit.getPNL(dateUtil.dateFormat(),localDatabase.retrieveAgentSerial()).enqueue(object :
            Callback<List<PnlModel>?> {
            override fun onResponse(
                call: Call<List<PnlModel>?>,
                response: Response<List<PnlModel>?>
            ) {
                val responseBody = response.body()!!
                responseBody.forEach(){
                    textViewTotalBet.text = formatter.format(it.totalBet.toDouble()).toString()+".00"
                    textViewTotalHits.text = formatter.format(it.totalHit.toDouble()).toString()+".00"
                    textViewPNL.text = formatter.format(it.pnl.toDouble()).toString()+".00"

                }
            }

            override fun onFailure(call: Call<List<PnlModel>?>, t: Throwable) {

            }
        })
    }

    private fun retrieveDraw2() {
        val retrofit = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
        retrofit.get2pmDraw(dateUtil.dateFormat(),localDatabase.retrieveAgentSerial(),localDatabase.retrieveDrawSerial("2 PM")).enqueue(object :
            Callback<List<DrawModel>?> {
            override fun onResponse(
                call: Call<List<DrawModel>?>,
                response: Response<List<DrawModel>?>
            ) {
                val responseBody = response.body()!!
                responseBody.forEach(){
                    textView2pmResult.text = it.result
                    textView2pmTotalWin.text = formatter.format(it.totalHit.toDouble()).toString()+".00"
                }
            }

            override fun onFailure(call: Call<List<DrawModel>?>, t: Throwable) {

            }
        })
    }

    private fun retrieveDraw5() {
        val retrofit = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
        retrofit.get5pmDraw("2022-11-15","7a1a5def-4222-40db-84d0-10461202d867",localDatabase.retrieveDrawSerial("5 PM")).enqueue(object :
            Callback<List<DrawModel>?> {
            override fun onResponse(
                call: Call<List<DrawModel>?>,
                response: Response<List<DrawModel>?>
            ) {
                val responseBody = response.body()!!
                responseBody.forEach(){
                    textView5pmResult.text = it.result
                    textView5pmTotalWin.text = formatter.format(it.totalHit.toDouble()).toString()+".00"
                }
            }

            override fun onFailure(call: Call<List<DrawModel>?>, t: Throwable) {

            }
        })
    }

    private fun retrieveDraw9() {
        val retrofit = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
        retrofit.get9pmDraw("2022-11-15","7a1a5def-4222-40db-84d0-10461202d867",localDatabase.retrieveDrawSerial("9 PM")).enqueue(object :
            Callback<List<DrawModel>?> {
            override fun onResponse(
                call: Call<List<DrawModel>?>,
                response: Response<List<DrawModel>?>
            ) {
                val responseBody = response.body()!!
                responseBody.forEach(){
                    textView9pmResult.text = it.result
                    textView9pmTotalWin.text = formatter.format(it.totalHit.toDouble()).toString()+".00"
                }
            }

            override fun onFailure(call: Call<List<DrawModel>?>, t: Throwable) {

            }
        })
    }

}