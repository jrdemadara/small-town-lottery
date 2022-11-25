package com.slicksoftcoder.smalltownlottery.features.dashboard

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.slicksoftcoder.smalltownlottery.R
import com.slicksoftcoder.smalltownlottery.common.model.ResultUpdateModel
import com.slicksoftcoder.smalltownlottery.features.authenticate.AuthenticateActivity
import com.slicksoftcoder.smalltownlottery.features.bet.BetActivity
import com.slicksoftcoder.smalltownlottery.features.history.HistoryActivity
import com.slicksoftcoder.smalltownlottery.features.result.ResultActivity
import com.slicksoftcoder.smalltownlottery.features.transmit.TransmitActivity
import com.slicksoftcoder.smalltownlottery.features.userprofile.UserProfileActivity
import com.slicksoftcoder.smalltownlottery.server.ApiInterface
import com.slicksoftcoder.smalltownlottery.server.LocalDatabase
import com.slicksoftcoder.smalltownlottery.server.NodeServer
import com.slicksoftcoder.smalltownlottery.util.DateUtil
import com.slicksoftcoder.smalltownlottery.util.NetworkChecker
import kotlinx.coroutines.*
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
    val formatter: NumberFormat = DecimalFormat("#,###")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        networkChecker = NetworkChecker(application)
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
            } else if (item.itemId == R.id.dashqr){
                barcodeLauncher.launch(ScanOptions())
            }
            false
        }

        lifecycleScope.launch(Dispatchers.IO){
            val pnl = async { localDatabase.retrievePNL(dateUtil.dateFormat()) }
            val result2pm = async { localDatabase.retrieve2pmResult(dateUtil.dateFormat()) }
            val result5pm = async { localDatabase.retrieve5pmResult(dateUtil.dateFormat()) }
            val result9pm = async { localDatabase.retrieve9pmResult(dateUtil.dateFormat()) }
            withContext(Dispatchers.Main){
                retrievePNLOffline(pnl.await())
                retrieve2pmResultOffline(result2pm.await())
                retrieve5pmResultOffline(result5pm.await())
                retrieve9pmResultOffline(result9pm.await())
            }
            withContext(Dispatchers.Main){
                retrievePNLOffline(pnl.await())
                retrieve2pmResultOffline(result2pm.await())
                retrieve5pmResultOffline(result5pm.await())
                retrieve9pmResultOffline(result9pm.await())
            }
        }

        textViewDate.text = dateUtil.currentDateShort().replace("-", " ").uppercase(Locale.ROOT)
        middleNavigation()
        bottomNavigation()
        cardView2PM()
        cardView5PM()
        cardView9PM()
    }

    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            Toast.makeText(this@DashboardActivity, "Cancelled", Toast.LENGTH_LONG).show()
            resultStatus("Scanner Cancelled", "QR Scanner has been cancelled", 0)
        } else {
            localDatabase.claimBet(result.contents)
            showSuccess()
        }
    }

    private fun showSuccess(){
        val dialog = Dialog(this)
        val view = layoutInflater.inflate(R.layout.success_dialog, null)
        dialog.setCancelable(true)
        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.setContentView(view)
        dialog.show()
        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
        }, 1000)

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
            val textViewDialogWinner: TextView = view.findViewById(R.id.textViewDialogWinner)
            val data = localDatabase.retrieve2pmResult(dateUtil.dateFormat())
            val list: ArrayList<Draw2pmModel> = data
            list.forEach {
                textViewTotalBet.text = formatter.format(it.totalBet.toDouble()).toString() + ".00"
                textViewTotalHit.text = formatter.format(it.totalHit.toDouble()).toString() + ".00"
                textViewPNL.text = formatter.format(it.pnl.toDouble()).toString() + ".00"
                textViewWinner.text = it.win
                textViewResult.text = "#"+it.result
                textViewDrawTime.text = "DETAILED 2 PM RESULT"
                if (it.win.toInt() > 1){
                    textViewDialogWinner.text = "WINNERS"
                }else{
                    textViewDialogWinner.text = "WINNER"
                }
                if (it.pnl.toDouble() <= 0){
                    textViewPNL.setTextColor(Color.parseColor("#E20A2A"))
                }else{
                    textViewPNL.setTextColor(Color.parseColor("#41B134"))
                }
            }
        }
    }

    private fun cardView5PM() {
        cardView5pm.setOnClickListener {
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
            val textViewDialogWinner: TextView = view.findViewById(R.id.textViewDialogWinner)
            val data = localDatabase.retrieve5pmResult(dateUtil.dateFormat())
            val list: ArrayList<Draw5pmModel> = data
            list.forEach {
                textViewTotalBet.text = formatter.format(it.totalBet.toDouble()).toString() + ".00"
                textViewTotalHit.text = formatter.format(it.totalHit.toDouble()).toString() + ".00"
                textViewPNL.text = formatter.format(it.pnl.toDouble()).toString() + ".00"
                textViewWinner.text = it.win
                textViewResult.text = "#"+it.result
                textViewDrawTime.text = "DETAILED 5 PM RESULT"
                if (it.win.toInt() > 1){
                    textViewDialogWinner.text = "WINNERS"
                }else{
                    textViewDialogWinner.text = "WINNER"
                }
                if (it.pnl.toDouble() <= 0){
                    textViewPNL.setTextColor(Color.parseColor("#E20A2A"))
                }else{
                    textViewPNL.setTextColor(Color.parseColor("#41B134"))
                }
            }
        }
    }

    private fun cardView9PM() {
        cardView9pm.setOnClickListener {
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
            val textViewDialogWinner: TextView = view.findViewById(R.id.textViewDialogWinner)
            val data = localDatabase.retrieve9pmResult(dateUtil.dateFormat())
            val list: ArrayList<Draw9pmModel> = data
            list.forEach {
                textViewTotalBet.text = formatter.format(it.totalBet.toDouble()).toString() + ".00"
                textViewTotalHit.text = formatter.format(it.totalHit.toDouble()).toString() + ".00"
                textViewPNL.text = formatter.format(it.pnl.toDouble()).toString() + ".00"
                textViewWinner.text = it.win
                textViewResult.text = "#"+it.result
                textViewDrawTime.text = "DETAILED 9 PM RESULT"
                if (it.win.toInt() > 1){
                    textViewDialogWinner.text = "WINNERS"
                }else{
                    textViewDialogWinner.text = "WINNER"
                }
                if (it.pnl.toDouble() <= 0){
                    textViewPNL.setTextColor(Color.parseColor("#E20A2A"))
                }else{
                    textViewPNL.setTextColor(Color.parseColor("#41B134"))
                }
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
                R.id.download -> {
                    updateResults()
                    true
                }
                else -> {
                    true
                }
            }
        }
    }

    suspend fun retrievePNLOffline(data: ArrayList<PnlModel>) {
        delay(100L)
        val list: ArrayList<PnlModel> = data
        var totalBet = 0.0
        var totalHit = 0.0
        var pnl = 0.0
        list.forEach {
            totalBet += it.totalBet.toDouble()
            totalHit += it.totalHit.toDouble()
            pnl = it.pnl.toDouble()
        }
        if (pnl < 0){
            textViewPNL.setTextColor(Color.parseColor("#E20A2A"))
        }else{
            textViewPNL.setTextColor(Color.parseColor("#41B134"))
        }
        delay(200L)
        textViewTotalBet.text = formatter.format(totalBet).toString()+".00"
        textViewTotalHits.text = formatter.format(totalHit).toString()+".00"
        textViewPNL.text = formatter.format(pnl).toString()+".00"
    }

    suspend fun retrieve2pmResultOffline(data: ArrayList<Draw2pmModel>) {
        delay(100L)
        val list: ArrayList<Draw2pmModel> = data
        list.forEach {
            textView2pmResult.text = it.result
            textView2pmTotalWin.text = formatter.format(it.totalHit.toDouble()).toString()+".00"
            if (it.win > 1.toString()){
                textView2pmWinner.text = it.win + " winners"
            }else{
                textView2pmWinner.text = it.win + " winner"
            }
        }
    }

    suspend fun retrieve5pmResultOffline(data: ArrayList<Draw5pmModel>) {
        delay(100L)
        val list: ArrayList<Draw5pmModel> = data
        list.forEach {
            textView5pmResult.text = it.result
            textView5pmTotalWin.text = formatter.format(it.totalHit.toDouble()).toString()+".00"
            if (it.win > 1.toString()){
                textView5pmWinner.text = it.win + " winners"
            }else{
                textView5pmWinner.text = it.win + " winner"
            }
        }
    }

    suspend fun retrieve9pmResultOffline(data: ArrayList<Draw9pmModel>) {
        delay(100L)
        val list: ArrayList<Draw9pmModel> = data
        list.forEach {
            textView9pmResult.text = it.result
            textView9pmTotalWin.text = formatter.format(it.totalHit.toDouble()).toString()+".00"
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

    private fun updateResults(){
        localDatabase.truncateResults()
        val retrofit = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
        retrofit.updateResults().enqueue(object : Callback<List<ResultUpdateModel>?> {
            override fun onResponse(
                call: Call<List<ResultUpdateModel>?>,
                response: Response<List<ResultUpdateModel>?>
            ) {
                resultStatus("Loading Success", "Updated results has been loaded.", 1)
                val list: List<ResultUpdateModel?>?
                list = response.body()
                assert(list != null)
                if (list != null) {
                    for (x in list) {
                        println(x)
                        localDatabase.updateResults(
                            x.serial,
                            x.drawSerial,
                            dateUtil.dateFormat(),
                            x.winningNumber,
                            dateUtil.dateFormat()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<List<ResultUpdateModel>?>, t: Throwable) {
                resultStatus("Loading Failed", "Error loading data from the server, Please Try again", 0)
            }
        })
    }

    private fun resultStatus(title: String?, detail: String?, isSuccess: Int?){
        val dialog = Dialog(this)
        val view = layoutInflater.inflate(R.layout.custom_toast_dialog, null)
        dialog.setCancelable(true)
        dialog.window?.attributes?.windowAnimations = R.style.TopDialogAnimation
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.TOP)
        dialog.setContentView(view)
        dialog.show()
        val textViewTitle: TextView = view.findViewById(R.id.textViewToastTitle)
        val textViewDetails: TextView = view.findViewById(R.id.textViewToastDetails)
        val imageView: ImageView = view.findViewById(R.id.imageViewToast)
        textViewTitle.text = title
        textViewDetails.text = detail
        if (isSuccess == 0){
            imageView.setImageResource(R.drawable.exclamation)
        }else{
            imageView.setImageResource(R.drawable.ic_round_check_circle_24)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
        }, 3000)
    }


    suspend fun checkNetworkConnection(): String? {
        delay(1000L)
        var status: String? = null
        networkChecker = NetworkChecker(application)
        networkChecker.observe(this) { isConnected ->
            status = if (isConnected) {
                "true"
                //imageViewStatus.setImageResource(R.drawable.online)
            } else {
                "false"
            }
        }
        return status
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