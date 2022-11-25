package com.slicksoftcoder.smalltownlottery.features.history

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
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
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.khairo.escposprinter.EscPosPrinter
import com.khairo.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.khairo.escposprinter.textparser.PrinterTextParserImg
import com.slicksoftcoder.smalltownlottery.R
import com.slicksoftcoder.smalltownlottery.features.bet.BetDetailsModel
import com.slicksoftcoder.smalltownlottery.features.dashboard.DashboardActivity
import com.slicksoftcoder.smalltownlottery.server.ApiInterface
import com.slicksoftcoder.smalltownlottery.server.LocalDatabase
import com.slicksoftcoder.smalltownlottery.server.NodeServer
import com.slicksoftcoder.smalltownlottery.util.DateUtil
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class HistoryActivity : AppCompatActivity() {
    private lateinit var localDatabase: LocalDatabase
    private lateinit var dateUtil: DateUtil
    private lateinit var recyclerViewHistory: RecyclerView
    private lateinit var textViewHistoryDate: TextView
    private var adapterHistory: HistoryAdapter? = null
    private lateinit var floatingButtonHistoryBack: FloatingActionButton
    private val formatter: NumberFormat = DecimalFormat("#,###")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        localDatabase = LocalDatabase(this)
        dateUtil = DateUtil()
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory)
        floatingButtonHistoryBack = findViewById(R.id.floatingButtonHistoryBack)
        textViewHistoryDate = findViewById(R.id.textViewHistoryDate)
        val toolbar = findViewById<View>(R.id.materialToolbarHistory) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.qrcode) {
                barcodeLauncher.launch(ScanOptions())
            }
            false
        }
        recyclerViewHistory.layoutManager = LinearLayoutManager(this)
        adapterHistory = HistoryAdapter()
        recyclerViewHistory.adapter = adapterHistory
        textViewHistoryDate.text = dateUtil.currentDateShort().replace("-", " ").uppercase(Locale.ROOT)
        retrieveHistory()
        floatingButtonHistoryBack.setOnClickListener {
            val intent = Intent(this, DashboardActivity ::class.java)
            startActivity(intent)
            finish()
        }

        adapterHistory!!.setOnClickItem {
            dialogBets(it.headerSerial,it.transactionCode,it.drawDate,it.drawTime,it.totalAmount,it.isVoid, it.betTime)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_history, menu)
        return true
    }

    private fun retrieveHistory(){
        val list = localDatabase.retrieveHistory()
        adapterHistory?.addItems(list)
    }

    private fun dialogBets(
        headerSerial: String,
        transaction: String,
        date: String,
        draw: String,
        amount: String,
        status: String,
        betTime: String
    ) {
        val dialog = Dialog(this)
        val view = layoutInflater.inflate(R.layout.history_bet_dialog, null)
        dialog.setCancelable(true)
        dialog.window?.attributes?.windowAnimations = R.style.BottomDialogAnimation
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.setContentView(view)
        dialog.show()
        var adapterHistoryBet: HistoryBetAdapter? = null
        val recyclerViewHistoryBet: RecyclerView = view.findViewById(R.id.recyclerViewHistoryBets)
        val textViewHistoryBetTransaction: TextView = view.findViewById(R.id.textViewHistoryBetTranscode)
        val textViewHistoryBetDate: TextView = view.findViewById(R.id.textViewHistoryBetDate)
        val textViewHistoryBetDraw: TextView = view.findViewById(R.id.textViewHistoryBetDraw)
        val textViewHistoryBetTotalAmount: TextView = view.findViewById(R.id.textViewHistoryBetTotalAmount)
        val textViewHistoryBetStatus: TextView = view.findViewById(R.id.textViewHistoryBetStatus)
        val buttonHistoryBetPrint: TextView = view.findViewById(R.id.buttonHistoryBetPrint)
        val buttonHistoryBetVoid: TextView = view.findViewById(R.id.buttonHistoryBetVoid)
        recyclerViewHistoryBet.layoutManager = LinearLayoutManager(this)
        adapterHistoryBet = HistoryBetAdapter()
        recyclerViewHistoryBet.adapter = adapterHistoryBet
        val list = localDatabase.retrieveHistoryBetDetails(headerSerial)
        adapterHistoryBet.addItems(list)
        textViewHistoryBetTransaction.text = transaction
        textViewHistoryBetDate.text = date
        textViewHistoryBetDraw.text = draw
        textViewHistoryBetTotalAmount.text = amount

        if (status.isEmpty()){
            if (0 == status.toInt()){
                textViewHistoryBetStatus.text = "ACTIVE"
                buttonHistoryBetVoid.text = "VOID"
            }else{
                textViewHistoryBetStatus.text = "VOID"
                buttonHistoryBetVoid.text = "DEVOID"
            }
        }
        buttonHistoryBetPrint.setOnClickListener {
            printReceipt(headerSerial, date,draw,betTime, transaction, formatter.format(amount.toDouble()).toString() + ".00")
        }
        buttonHistoryBetVoid.setOnClickListener {
            if (status.toInt() == 0){
                alertDialog(transaction, headerSerial,dialog,0)
            }else{
                alertDialog(transaction, headerSerial,dialog,1)
            }
        }
    }

    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            resultStatus("Scanner Cancelled", "QR Scanner has been cancelled", 0)
        } else {
            val data = localDatabase.retrieveHistoryByTranscode(result.contents)
            val list: ArrayList<HistoryModel> = data
            var betTime: String? = null
            var drawDate: String? = null
            var drawTime: String? = null
            var headerSerial: String? = null
            var totalAmount: String? = null
            var transaction: String? = null
            var status: String? = null
            if (list.size > 0){
                list.forEach {
                    betTime = it.betTime
                    drawDate = it.drawDate
                    drawTime = it.drawTime
                    headerSerial = it.headerSerial
                    totalAmount = it.totalAmount
                    transaction = it.transactionCode
                    status = it.isVoid
                }
                dialogBets(headerSerial.toString(),transaction.toString(),drawDate.toString(),drawTime.toString(),totalAmount.toString(),status.toString(), betTime.toString())
            }else{
                resultStatus("Invalid Receipt", "Transaction number ${result.contents} is invalid.", 0)
            }
        }
    }

    private fun voidBet(headerSerial: String, void: Int){
        val retIn = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
        retIn.voidBetHeader(headerSerial, void).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {

                }else{
                    localDatabase.updateBetHeaderTransmitted(headerSerial, dateUtil.dateFormat() + " " + dateUtil.currentTimeComplete(), 0)
                    localDatabase.updateBetDetailsTransmitted(headerSerial, dateUtil.dateFormat() + " " + dateUtil.currentTimeComplete(), 0)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                localDatabase.updateBetHeaderTransmitted(headerSerial, dateUtil.dateFormat() + " " + dateUtil.currentTimeComplete(), 0)
                localDatabase.updateBetDetailsTransmitted(headerSerial, dateUtil.dateFormat() + " " + dateUtil.currentTimeComplete(), 0)
            }
        })
    }

    private fun alertDialog(transaction: String, headerSerial: String, dialog: Dialog, isVoid: Int){
        val voidStatus: Int
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Void Bet")
        voidStatus = if (isVoid == 0){
            builder.setMessage("Are you sure you want to void $transaction?")
            1
        }else{
            builder.setMessage("Are you sure you want to devoid $transaction?")
            0
        }
        builder.setIcon(R.drawable.resource_void)
        builder.setPositiveButton("Yes"){ dialogInterface, _ ->
            localDatabase.voidBet(headerSerial, voidStatus)
            voidBet(headerSerial, voidStatus)
            dialogInterface.dismiss()
            retrieveHistory()
            resultStatus("Bet Status", "$transaction has been updated.", 0)
            dialog.dismiss()
        }
        builder.setNeutralButton("No"){ dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_PRIVILEGED
    )
    private val PERMISSIONS_LOCATION = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_PRIVILEGED
    )

    private fun checkPermissions() {
        val permission1 =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
        val permission2 =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)

        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_STORAGE,
                1
            )
        } else if (permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_LOCATION,
                1
            )
        }
    }

    private fun printReceipt(headerSerial: String , drawDate: String, drawTime:String, betTime: String, transCode: String, totalAmount: String){
        val agent = localDatabase.retrieveAgentName()
        val location = localDatabase.retrieveLocation()
        val data = localDatabase.retrieveBetDetails(headerSerial)
        val list: ArrayList<BetDetailsModel> = data
        var bets = ""

        list.forEach {
            bets += "[L]${it.betNumber}[C]${formatter.format(it.win.toDouble())}[R]${it.amount+".00"}\n"
        }
        checkPermissions()
        val printer = EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 48f, 32)
        printer
            .printFormattedText("[C]<img>${
                PrinterTextParserImg.bitmapToHexadecimalString(printer, this.applicationContext.resources.getDrawableForDensity(
                R.drawable.recieptlogo, DisplayMetrics.DENSITY_MEDIUM))}</img>\n" +
                    "[L]\n" +
                    "[C]<u><font size='tall'>Small Town Lottery</font></u>\n" +
                    "[C]<u><font size='normal'>Just Explore SDT</font></u>\n" + "[L]\n"+
                    "[L]<b>Agent:</b>[R]<b>${agent.uppercase(Locale.ROOT)}</b>\n" +
                    "[L]<b>Area:</b>[R]<b>$location</b>\n" +
                    "[L]<b>Draw Date:</b>[R]<b>$drawDate</b>\n" +
                    "[L]<b>Bet Time:</b>[R]<b>$betTime</b>\n" +
                    "[L]<b>Draw Time:</b>[R]<b>$drawTime</b>\n" +
                    "[L]<b>Trans Code:[R]<font size='normal'>$transCode</font>\n" +
                    "[C]--------------------------------\n" +
                    "[L]BET[C]WIN[R]AMOUNT\n" +
                    "[C]--------------------------------\n" +
                    "$bets" +
                    "[C]--------------------------------\n" +
                    "[L]<b>TOTAL AMOUNT:</b>[R]<b>$totalAmount</b>\n" +
                    "[C]--------------------------------\n" +
                    "[C]<qrcode size='20'>$transCode</qrcode>\n" +
                    "[L]\n" +
                    "[C]Thank You! Bet Again!\n".trimIndent()
            )
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

}

