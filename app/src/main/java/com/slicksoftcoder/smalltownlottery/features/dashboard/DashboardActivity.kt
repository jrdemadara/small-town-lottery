package com.slicksoftcoder.smalltownlottery.features.dashboard

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.slicksoftcoder.smalltownlottery.R
import com.slicksoftcoder.smalltownlottery.features.authenticate.AuthenticateActivity
import com.slicksoftcoder.smalltownlottery.features.bet.BetActivity
import com.slicksoftcoder.smalltownlottery.features.history.HistoryActivity
import com.slicksoftcoder.smalltownlottery.features.result.ResultActivity
import com.slicksoftcoder.smalltownlottery.features.transmit.TransmitActivity
import com.slicksoftcoder.smalltownlottery.features.userprofile.UserProfileActivity
import com.slicksoftcoder.smalltownlottery.server.LocalDatabase
import com.slicksoftcoder.smalltownlottery.util.DateUtil
import com.slicksoftcoder.smalltownlottery.util.NetworkChecker
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
    private lateinit var textViewMenuTransmit: TextView
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
    private lateinit var imageViewStatus: ImageView
    private lateinit var imageViewBet: ImageView
    private lateinit var agentSerial: String
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
        textViewMenuTransmit = findViewById(R.id.textViewDashTransmit)
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
        imageViewStatus = findViewById(R.id.imageViewDashStatus)
        val toolbar = findViewById<View>(R.id.materialToolbarDash) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false);
        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.logout) {
                logoutUser()
            } else if (item.itemId == R.id.profile) {
                userProfile()
            }
            false
        }
        textViewDate.text = dateUtil.currentDateShort().replace("-", " ").uppercase(Locale.ROOT)
        middleNavigation()
        bottomNavigation()
        checkNetworkConnection()
        retrievePNLOffline()
        retrieve2pmResultOffline()
        retrieve5pmResultOffline()
        retrieve9pmResultOffline()
//        cardView2PM()
    }

    private fun cardView2PM() {
        cardView2pm.setOnClickListener {
            val dialog = Dialog(this)
            val view = layoutInflater.inflate(R.layout.detailed_result_dialog, null)
            dialog.setCancelable(true)
            dialog.window?.attributes?.windowAnimations = R.style.BottomDialogAnimation
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setGravity(Gravity.BOTTOM)
            dialog.setContentView(view)
            dialog.show()
            val textViewTotalBet: TextView = view.findViewById(R.id.textViewDialogResultBet)
            val textViewTotalHit: TextView = view.findViewById(R.id.textViewDialogResultHit)
            val textViewPNL: TextView = view.findViewById(R.id.textViewDialogResultPNL)
            val textViewWinner: TextView = view.findViewById(R.id.textViewDialogResultWinner)
            val textViewResult: TextView = view.findViewById(R.id.textViewDialogResult)
            val textViewDrawTime: TextView = view.findViewById(R.id.textViewDialogResultHeader)
            val data = localDatabase.retrieve2pmResult(dateUtil.dateFormat())
            val list: ArrayList<Draw2pmModel> = data
            list.forEach {
                textViewTotalBet.text = it.totalBet
                textViewTotalHit.text = it.totalHit
                textViewPNL.text = it.pnl
                textViewWinner.text = it.win
                textViewResult.text = it.result
                textViewDrawTime.text = "2 PM"
            }
        }
    }

    private fun bottomNavigation() {
        imageViewBet.setOnClickListener {
            val intent = Intent(this, BetActivity::class.java)
            startActivity(intent)
            finish()
        }
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.history -> {
                    val intent = Intent(this, HistoryActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.bet -> {
                    val intent = Intent(this, BetActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.result -> {
                    val intent = Intent(this, ResultActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.settings -> {
                    val intent = Intent(this, DashboardActivity::class.java)
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

    private fun retrievePNLOffline() {
        val data = localDatabase.retrievePNL(dateUtil.dateFormat())
        val list: ArrayList<PnlModel> = data
        var totalBet = 0.0
        var totalHit = 0.0
        var pnl = 0.0
        list.forEach {
            totalBet += it.totalBet.toDouble()
            totalHit += it.totalHit.toDouble()
            pnl += it.pnl.toDouble()
        }
        if (pnl < 0){
            textViewPNL.setTextColor(Color.parseColor("#E20A2A"))
        }else{
            textViewPNL.setTextColor(Color.parseColor("#41B134"))
        }
        textViewTotalBet.text = formatter.format(totalBet).toString()+".00"
        textViewTotalHits.text = formatter.format(totalHit).toString()+".00"
        textViewPNL.text = formatter.format(pnl).toString()+".00"
    }

    private fun retrieve2pmResultOffline() {
        val data = localDatabase.retrieve2pmResult(dateUtil.dateFormat())
        val list: ArrayList<Draw2pmModel> = data
        list.forEach {
            textView2pmResult.text = it.result
            textView2pmTotalWin.text = it.totalHit
            if (it.win > 1.toString()){
                textView2pmWinner.text = it.win + " winners"
            }else{
                textView2pmWinner.text = it.win + " winner"
            }
        }
    }

    private fun retrieve5pmResultOffline() {
        val data = localDatabase.retrieve5pmResult(dateUtil.dateFormat())
        val list: ArrayList<Draw5pmModel> = data
        list.forEach {
            textView5pmResult.text = it.result
            textView5pmTotalWin.text = it.totalHit
            if (it.win > 1.toString()){
                textView5pmWinner.text = it.win + " winners"
            }else{
                textView5pmWinner.text = it.win + " winner"
            }
        }
    }

    private fun retrieve9pmResultOffline() {
        val data = localDatabase.retrieve9pmResult(dateUtil.dateFormat())
        val list: ArrayList<Draw9pmModel> = data
        list.forEach {
            textView9pmResult.text = it.result
            textView9pmTotalWin.text = it.totalHit
            if (it.win > 1.toString()){
                textView9pmWinner.text = it.win + " winners"
            }else{
                textView9pmWinner.text = it.win + " winner"
            }
        }
    }

    private fun dialogCutoff(){
        val dialog = Dialog(this)
        val view = layoutInflater.inflate(R.layout.custoff_dialog, null)
        dialog.setCancelable(true)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun middleNavigation() {
        textViewMenuBet.setOnClickListener {
            val currentTime = dateUtil.currentTimeComplete()
            val cutoff2 = localDatabase.retrieveDrawCutOff("2 PM")
            val resume2 = localDatabase.retrieveDrawResume("2 PM")
            val cutoff5 = localDatabase.retrieveDrawCutOff("5 PM")
            val resume5 = localDatabase.retrieveDrawResume("5 PM")
            val cutoff9 = localDatabase.retrieveDrawCutOff("9 PM")
            val resume9 = localDatabase.retrieveDrawResume("9 PM")

            if (currentTime > cutoff2 && currentTime < resume2) {
                dialogCutoff()
            } else if (currentTime > cutoff2 && currentTime > resume2 ) {
                val intent = Intent(this, BetActivity::class.java)
                startActivity(intent)
                finish()
            }else if (currentTime < cutoff2){
                val intent = Intent(this, BetActivity::class.java)
                startActivity(intent)
                finish()
            }

            if (currentTime > cutoff5 && currentTime < resume5) {
                dialogCutoff()
            } else if (currentTime > cutoff5 && currentTime > resume5 ) {
                val intent = Intent(this, BetActivity::class.java)
                startActivity(intent)
                finish()
            }else if (currentTime < cutoff5){
                val intent = Intent(this, BetActivity::class.java)
                startActivity(intent)
                finish()
            }

            if (currentTime > cutoff9 && currentTime < resume9) {
                dialogCutoff()
            } else if (currentTime > cutoff9 && currentTime > resume9 ) {
                val intent = Intent(this, BetActivity::class.java)
                startActivity(intent)
                finish()
            }else if (currentTime < cutoff9){
                val intent = Intent(this, BetActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        textViewMenuHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
            finish()
        }

        textViewMenuResult.setOnClickListener {
            val intent = Intent(this, ResultActivity::class.java)
            startActivity(intent)
            finish()
        }
        textViewMenuTransmit.setOnClickListener {
            val intent = Intent(this, TransmitActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

//    Deprecated Feature

//    private fun retrievePNL() {
//        val retrofit = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
//        retrofit.getPNL(dateUtil.dateFormat(),localDatabase.retrieveAgent()).enqueue(object :
//            Callback<List<PnlModel>?> {
//            override fun onResponse(
//                call: Call<List<PnlModel>?>,
//                response: Response<List<PnlModel>?>
//            ) {
//                val responseBody = response.body()!!
//                responseBody.forEach(){
//                    textViewTotalBet.text = formatter.format(it.totalBet.toDouble()).toString()+".00"
//                    textViewTotalHits.text = formatter.format(it.totalHit.toDouble()).toString()+".00"
//                    textViewPNL.text = formatter.format(it.pnl.toDouble()).toString()+".00"
//                    if (it.pnl.contains("-")){
//                        textViewPNL.setTextColor(Color.parseColor("#E20A2A"))
//                    }else{
//                        textViewPNL.setTextColor(Color.parseColor("#41B134"))
//                    }
//
//
//                }
//            }
//
//            override fun onFailure(call: Call<List<PnlModel>?>, t: Throwable) {
//                retrievePNLOffline()
//            }
//        })
//    }

//    private fun retrieveDraw2() {
//        val retrofit = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
//        retrofit.get2pmDraw(
//            dateUtil.dateFormat(),
//            localDatabase.retrieveAgent(),
//            localDatabase.retrieveDrawSerial("2 PM")
//        ).enqueue(object :
//            Callback<List<Draw2pmModel>?> {
//            override fun onResponse(
//                call: Call<List<Draw2pmModel>?>,
//                response: Response<List<Draw2pmModel>?>
//            ) {
//                val responseBody = response.body()!!
//                responseBody.forEach() {
//                    textView2pmResult.text = it.result
//                    textView2pmTotalWin.text =
//                        formatter.format(it.totalHit.toDouble()).toString() + ".00"
//                    if (it.win.toDouble() > 1) {
//                        textView2pmWinner.text =
//                            it.win.replace(".", "").replace("00", "") + " winners"
//                    } else {
//                        textView2pmWinner.text =
//                            it.win.replace(".", "").replace("00", "") + " winner"
//                    }
//
//                }
//            }
//
//            override fun onFailure(call: Call<List<Draw2pmModel>?>, t: Throwable) {
//                retrieve2pmResultOffline()
//            }
//        })
//    }

//    private fun retrieveDraw5() {
//        val retrofit = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
//        retrofit.get5pmDraw(
//            dateUtil.dateFormat(),
//            localDatabase.retrieveAgent(),
//            localDatabase.retrieveDrawSerial("5 PM")
//        ).enqueue(object :
//            Callback<List<Draw5pmModel>?> {
//            override fun onResponse(
//                call: Call<List<Draw5pmModel>?>,
//                response: Response<List<Draw5pmModel>?>
//            ) {
//                val responseBody = response.body()!!
//                responseBody.forEach() {
//                    textView5pmResult.text = it.result
//                    textView5pmTotalWin.text =
//                        formatter.format(it.totalHit.toDouble()).toString() + ".00"
//                    if (it.win.toDouble() > 1) {
//                        textView5pmWinner.text =
//                            it.win.replace(".", "").replace("00", "") + " winners"
//                    } else {
//                        textView5pmWinner.text =
//                            it.win.replace(".", "").replace("00", "") + " winner"
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<List<Draw5pmModel>?>, t: Throwable) {
//
//            }
//        })
//    }
//
//    private fun retrieveDraw9() {
//        val retrofit = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
//        retrofit.get9pmDraw(
//            dateUtil.dateFormat(),
//            localDatabase.retrieveAgent(),
//            localDatabase.retrieveDrawSerial("9 PM")
//        ).enqueue(object :
//            Callback<List<Draw9pmModel>?> {
//            override fun onResponse(
//                call: Call<List<Draw9pmModel>?>,
//                response: Response<List<Draw9pmModel>?>
//            ) {
//                val responseBody = response.body()!!
//                responseBody.forEach() {
//                    textView9pmResult.text = it.result
//                    textView9pmTotalWin.text =
//                        formatter.format(it.totalHit.toDouble()).toString() + ".00"
//                    if (it.win.toDouble() > 1) {
//                        textView9pmWinner.text =
//                            it.win.replace(".", "").replace("00", "") + " winners"
//                    } else {
//                        textView9pmWinner.text =
//                            it.win.replace(".", "").replace("00", "") + " winner"
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<List<Draw9pmModel>?>, t: Throwable) {
//
//            }
//        })
//    }

    private fun checkNetworkConnection() {
        networkChecker = NetworkChecker(application)
        networkChecker.observe(this) { isConnected ->
            if (isConnected) {
                imageViewStatus.setImageResource(R.drawable.online)
//                retrievePNL()
//                retrieveDraw2()
//                retrieveDraw5()
//                retrieveDraw9()
            } else {
                imageViewStatus.setImageResource(R.drawable.offline)
//                retrievePNLOffline()
//                cardView2PM()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    private fun userProfile(){
        val intent = Intent(this, UserProfileActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun logoutUser(){
        val intent = Intent(this, AuthenticateActivity::class.java)
        startActivity(intent)
        finish()
    }

}