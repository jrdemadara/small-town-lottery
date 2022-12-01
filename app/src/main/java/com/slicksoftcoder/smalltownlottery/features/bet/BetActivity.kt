package com.slicksoftcoder.smalltownlottery.features.bet

import android.Manifest
import android.app.Dialog
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.khairo.escposprinter.EscPosPrinter
import com.khairo.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.khairo.escposprinter.textparser.PrinterTextParserImg
import com.muddassir.connection_checker.ConnectionState
import com.muddassir.connection_checker.checkConnection
import com.slicksoftcoder.smalltownlottery.R
import com.slicksoftcoder.smalltownlottery.common.model.BetDetailsTransmitModel
import com.slicksoftcoder.smalltownlottery.common.model.BetHeaderTransmitModel
import com.slicksoftcoder.smalltownlottery.features.dashboard.DashboardActivity
import com.slicksoftcoder.smalltownlottery.server.ApiInterface
import com.slicksoftcoder.smalltownlottery.server.LocalDatabase
import com.slicksoftcoder.smalltownlottery.server.NodeServer
import com.slicksoftcoder.smalltownlottery.util.DateUtil
import com.slicksoftcoder.smalltownlottery.util.NetworkChecker
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.properties.Delegates


class BetActivity : AppCompatActivity() {
    private lateinit var localDatabase: LocalDatabase
    private lateinit var dateUtil: DateUtil
    private lateinit var recyclerView: RecyclerView
    private var adapter: BetAdapter? = null
    private lateinit var imageViewStatus: ImageView
    private lateinit var textViewDate: TextView
    private lateinit var textViewTime: TextView
    private lateinit var textViewTotal: TextView
    private lateinit var editTextBetNumber: EditText
    private lateinit var editTextBetAmount: EditText
    private lateinit var buttonBetAdd: Button
    private lateinit var buttonBetConfirm: Button
    private lateinit var buttonBetCancel: Button
    private var totalAmount by Delegates.notNull<Double>()
    private var winAmount by Delegates.notNull<Double>()
    private lateinit var drawTime: String
    private val formatter: NumberFormat = DecimalFormat("#,###")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bet)
        dateUtil = DateUtil()
        localDatabase = LocalDatabase(this)
        recyclerView = findViewById(R.id.recyclerViewBets)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = BetAdapter()
        recyclerView.adapter = adapter
        imageViewStatus = findViewById(R.id.imageViewStatusBet)
        textViewDate = findViewById(R.id.textViewBetDate)
        textViewTime = findViewById(R.id.textViewBetTime)
        textViewTotal = findViewById(R.id.textViewBetTotal)
        editTextBetNumber = findViewById(R.id.editTextBetNumber)
        editTextBetAmount = findViewById(R.id.editTextBetAmount)
        buttonBetAdd = findViewById(R.id.buttonBetAdd)
        buttonBetConfirm = findViewById(R.id.buttonBetConfirm)
        buttonBetCancel = findViewById(R.id.buttonBetCancel)
        textViewDate.text = dateUtil.currentDateShort().replace("-", " ").uppercase(Locale.ROOT)
        totalAmount = 0.0
        winAmount = 0.0
        drawTime = ""

        //* Check Internet Connection
        val connection = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connection.activeNetworkInfo
        if (activeNetwork != null){
            imageViewStatus.setImageResource(R.drawable.online)
        }else{
            imageViewStatus.setImageResource(R.drawable.offline)
        }
        checkConnection(this) { connectionState ->
            when(connectionState) {
                ConnectionState.CONNECTED -> {
                    imageViewStatus.setImageResource(R.drawable.online)
                }
                ConnectionState.SLOW -> {
                    imageViewStatus.setImageResource(R.drawable.slow)
                }
                else -> {
                    imageViewStatus.setImageResource(R.drawable.offline)
                }
            }
        }

        selectDrawTime()
        editTextBetNumber.requestFocus()
        textViewTime.setOnClickListener {
            selectDrawTime()
        }

        editTextBetNumber.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (editTextBetNumber.text.length == 3){
                    editTextBetAmount.requestFocus()
                }
            }
            override fun afterTextChanged(s: Editable?) {}

        })


        val headerSerial: UUID = UUID.randomUUID()
        buttonBetAdd.setOnClickListener {
                if (editTextBetNumber.text.isNotEmpty() && editTextBetAmount.text.isNotEmpty()){
                    val dialog = Dialog(this)
                    val view = layoutInflater.inflate(R.layout.bet_option_dialog, null)
                    dialog.setCancelable(true)
                    dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog.window?.setGravity(Gravity.CENTER)
                    dialog.setContentView(view)
                    dialog.show()
                    val radioButtonRegular: RadioButton = view.findViewById(R.id.radioButtonBetRegular)
                    val radioButtonRambolito: RadioButton = view.findViewById(R.id.radioButtonBetRambolito)
                    val buttonConfirm: Button = view.findViewById(R.id.buttonBetConfirmType)
                    val serial: UUID = UUID.randomUUID()
                    var isRambolito: Int = 0
                    val amount: Double = editTextBetAmount.text.toString().toDouble()
                    radioButtonRegular.setOnClickListener {
                        radioButtonRambolito.isChecked = false
                        isRambolito = 0
                    }
                    radioButtonRambolito.setOnClickListener {
                        radioButtonRegular.isChecked = false
                        isRambolito = 1
                    }

                    buttonConfirm.setOnClickListener {
                        if (radioButtonRegular.isChecked || radioButtonRambolito.isChecked){
                            if (isRambolito == 0){
                                winAmount =   2750 * (amount/5)
                                /* Save Bet */
                                localDatabase.insertBetDetails(serial.toString(),headerSerial.toString(),editTextBetNumber.text.toString(),editTextBetAmount.text.toString(), winAmount.toString(), isRambolito.toString())
                                val list = localDatabase.retrieveBetDetails(headerSerial.toString())
                                /* Retrieve Bet */
                                adapter?.addItems(list)
                                /* Update UI */
                                totalAmount += amount
                                textViewTotal.text = formatter.format(totalAmount).toString()+".00"
                                editTextBetNumber.setText("")
                                editTextBetAmount.setText("")
                                editTextBetNumber.requestFocus()
                                winAmount = 0.0
                                isRambolito = 0
                                resultStatus("Success", "Bet has been added.", 1)
                                dialog.dismiss()
                            }else{
                                if (amount >= 30){
                                    winAmount =  2750 * (amount/30)
                                    editTextBetAmount.setTextColor(Color.parseColor("#000000"))
                                    /* Save Bet */
                                    localDatabase.insertBetDetails(serial.toString(),headerSerial.toString(),editTextBetNumber.text.toString(),editTextBetAmount.text.toString(), winAmount.toString(), isRambolito.toString())
                                    val list = localDatabase.retrieveBetDetails(headerSerial.toString())
                                    /* Retrieve Bet */
                                    adapter?.addItems(list)
                                    /* Update UI */
                                    totalAmount += amount
                                    textViewTotal.text = formatter.format(totalAmount).toString()+".00"
                                    editTextBetNumber.setText("")
                                    editTextBetAmount.setText("")
                                    editTextBetNumber.requestFocus()
                                    winAmount = 0.0
                                    isRambolito = 0
                                    resultStatus("Success", "Bet has been added.", 1)
                                    dialog.dismiss()
                                }else{
                                    resultStatus("Warning", "Amount for rambolito should not be less than 30.", 0)
                                    editTextBetAmount.setTextColor(Color.parseColor("#F15555"))
                                    dialog.dismiss()
                                }
                            }
                        }else{
                            resultStatus("Warning", "Please select type.", 0)
                        }

                    }
                }else{
                    resultStatus("Warning", "Please fill the required fields.", 0)
                }

            buttonBetAdd.text = "Add Bet"
        }

            adapter?.setOnClickItem {
                val dialog = Dialog(this)
                val view = layoutInflater.inflate(R.layout.bet_action_dialog, null)
                dialog.setCancelable(true)
                dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.window?.setGravity(Gravity.CENTER)
                dialog.setContentView(view)
                dialog.show()
                val radioButtonBetActionEdit: RadioButton = view.findViewById(R.id.radioButtonBetActionEdit)
                val radioButtonBetActionDelete: RadioButton = view.findViewById(R.id.radioButtonBetActionDelete)
                val buttonBetActionConfirm: Button = view.findViewById(R.id.buttonBetActionConfirm)
                var action = ""
                val serial = it.serial
                val betNumber = it.betNumber
                val amount = it.amount
                radioButtonBetActionEdit.setOnClickListener {
                    action = "edit"
                }
                radioButtonBetActionDelete.setOnClickListener {
                    action = "delete"
                }
                buttonBetActionConfirm.setOnClickListener{
                    when (action) {
                        "edit" -> {
                            editTextBetNumber.setText(betNumber)
                            editTextBetAmount.setText(amount)
                            buttonBetAdd.text = "Edit Bet"
                            localDatabase.deleteBetDetail(serial)
                            totalAmount -= amount.toDouble()
                            textViewTotal.text =  formatter.format(totalAmount).toString()+".00"
                            dialog.dismiss()
                        }
                        "delete" -> {
                            localDatabase.deleteBetDetail(serial)
                            val list = localDatabase.retrieveBetDetails(headerSerial.toString())
                            adapter?.addItems(list)
                            totalAmount -= amount.toDouble()
                            textViewTotal.text =  formatter.format(totalAmount).toString()+".00"
                            dialog.dismiss()
                        }
                        else -> {
                            dialog.dismiss()
                        }
                    }
                }
            }


        buttonBetConfirm.setOnClickListener {
            if (totalAmount <= 0){
                resultStatus("Warning", "Please place a bet first.", 0)
            }else{
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Confirm")
                builder.setMessage("Do you wish to continue?")
                builder.setIcon(R.drawable.ic_round_check_circle_24)
                builder.setPositiveButton("Yes"){ dialogInterface, _ ->
                    val agent = localDatabase.retrieveAgent()
                    val draw = localDatabase.retrieveDrawSerial(drawTime)
                    val transCode: String = "B"+dateUtil.dateShort()+dateUtil.currentTimeComplete()+ agent.take(2)
                        .uppercase(Locale.ROOT)
                    val transCodeDash = transCode.replace("-","")
                    val transCodeCol = transCodeDash.replace(":","")
                    localDatabase.insertBetHeader(
                        headerSerial.toString(),
                        agent,
                        dateUtil.dateFormat(),
                        draw,
                        transCodeCol,
                        totalAmount.toString(),
                        dateUtil.dateFormat() + " " + dateUtil.currentTimeComplete()
                    )
                    localDatabase.confirmBetDetails(headerSerial.toString())
                    transmitBetHeader(headerSerial.toString())
                    transmitBetDetails(headerSerial.toString())
                    printReceipt(headerSerial.toString(), dateUtil.dateFormat(), drawTime, dateUtil.currentTime(), transCodeCol, formatter.format(totalAmount).toString() + ".00")
                    showSuccess()
                    Handler(Looper.getMainLooper()).postDelayed({
                        dialogInterface.dismiss()
                    }, 2000)
                }
                builder.setNeutralButton("Cancel"){ dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
            }



        }
        buttonBetCancel.setOnClickListener {
            if (totalAmount <= 0){
                localDatabase.deleteBet(headerSerial.toString())
                val intent = Intent(this, DashboardActivity ::class.java)
                startActivity(intent)
                finish()
            }else{
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Cancel")
                builder.setMessage("Are you sure you want to cancel?")
                builder.setIcon(R.drawable.ic_round_cancel_24)
                builder.setPositiveButton("Yes"){ dialogInterface, _ ->
                    localDatabase.deleteBet(headerSerial.toString())
                    val intent = Intent(this, DashboardActivity ::class.java)
                    startActivity(intent)
                    finish()
                    dialogInterface.dismiss()
                }
                builder.setNeutralButton("No"){ dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
            }


        }

    }

    private fun selectDrawTime(){
        val dialog = Dialog(this)
        val view = layoutInflater.inflate(R.layout.draw_option_dialog, null)
        dialog.setCancelable(false)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.setContentView(view)
        dialog.show()
        val radioButton2pm: RadioButton = view.findViewById(R.id.radioButtonBet2PM)
        val radioButton5pm: RadioButton = view.findViewById(R.id.radioButtonBet5PM)
        val radioButton9pm: RadioButton = view.findViewById(R.id.radioButtonBet9PM)
        val radioButtonConfirm: Button = view.findViewById(R.id.buttonBetConfirmDraw)
        var draw: String?
        val currentTime = dateUtil.currentTimeComplete()
        val cutoff2 = localDatabase.retrieveDrawCutOff("2 PM")
        val cutoff5 = localDatabase.retrieveDrawCutOff("5 PM")
        val cutoff9 = localDatabase.retrieveDrawCutOff("9 PM")
        if (currentTime >= cutoff2){
            radioButton2pm.isChecked = false
            radioButton2pm.isEnabled = false
            drawTime = "5 PM"
            draw = "5 PM"
        }else{
            radioButton2pm.isChecked = true
            drawTime = "2 PM"
            draw = "2 PM"
        }
        if (currentTime >= cutoff5){
            radioButton9pm.isChecked = true
            drawTime = "9 PM"
            draw = "9 PM"
        }else{
            radioButton5pm.isChecked = false
            radioButton5pm.isEnabled = false
            drawTime = "5 PM"
            draw = "5 PM"
        }
        if (currentTime >= cutoff9){
            resultStatus("Cutoff", "Bet will resume tomorrow.", 0)
            dialog.dismiss()
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, DashboardActivity ::class.java)
                startActivity(intent)
                finish()
            }, 3000)

        }else{
            radioButton9pm.isChecked = false
            radioButton9pm.isEnabled = false
            drawTime = "9 PM"
            draw = "9 PM"
        }

        radioButton2pm.setOnClickListener {
            radioButton5pm.isChecked = false
            radioButton9pm.isChecked = false
            drawTime = "2 PM"
            draw = "2 PM"
        }
        radioButton5pm.setOnClickListener {
            radioButton2pm.isChecked = false
            radioButton9pm.isChecked = false
            drawTime = "5 PM"
            draw = "5 PM"
        }
        radioButton9pm.setOnClickListener {
            radioButton5pm.isChecked = false
            radioButton2pm.isChecked = false
            drawTime = "9 PM"
            draw = "9 PM"
        }
        radioButtonConfirm.setOnClickListener {
            if (draw?.isNotEmpty() == true){
                textViewTime.text = draw
                dialog.dismiss()
            }else{
                resultStatus("No Draw Time", "Please select draw time", 0)
            }

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
            val intent = Intent(this, DashboardActivity ::class.java)
            startActivity(intent)
            finish()
        }, 1000)

    }

    private fun transmitBetHeader(headerSerial: String){
        val retrofit = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
        val data = localDatabase.transmitBetHeader(headerSerial)
        val list: ArrayList<BetHeaderTransmitModel> = data
        if (list.size > 0){
            list.forEach{
                val filter = HashMap<String, String>()
                filter["serial"] = it.serial
                filter["agent"] = it.agent
                filter["drawdate"] = it.drawDate
                filter["drawserial"] = it.drawSerial
                filter["transcode"] = it.transactionCode
                filter["totalamount"] = it.totalAmount
                filter["datecreated"] = it.dateCreated
                filter["dateprinted"] = it.datePrinted
                filter["isvoid"] = it.isVoid
                filter["editedby"] = it.editedBy
                filter["dateedited"] = it.dateEdited
                retrofit.transmitBetHeaders(filter).enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                        if (response.code() == 200) {
                            localDatabase.updateBetHeaderTransmitted(headerSerial,dateUtil.dateFormat() + " " + dateUtil.currentTimeComplete(), 1)
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {

                    }
                })
            }
        }
    }
    private fun transmitBetDetails(headerSerial: String){
        val retrofit = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
        val data = localDatabase.transmitBetDetail(headerSerial)
        val list: ArrayList<BetDetailsTransmitModel> = data
        if (list.size > 0){
            list.forEach{
                val filter = HashMap<String, String>()
                filter["serial"] = it.serial
                filter["headerserial"] = it.headerSerial
                filter["betno"] = it.betNumber
                filter["totalamount"] = it.amount
                filter["win"] = it.win
                filter["isrambolito"] = it.isRambolito
                retrofit.transmitBetDetails(filter).enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                        if (response.code() == 200) {
                            localDatabase.updateBetDetailTransmitted(it.serial,dateUtil.dateFormat() + " " + dateUtil.currentTimeComplete(), 1)
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {

                    }
                })
            }
        }
    }

    private val PERMISSION = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_PRIVILEGED
    )


    private fun checkPermissions() {
        val permission1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
        val permission2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSION, 1)
        }else if (permission2 != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, PERMISSION, 1)
        }
    }

    private fun printReceipt(headerSerial: String , drawDate: String, drawTime:String, betTime: String, transCode: String, totalAmount: String){
        val bluetoothManager = applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
        if (!bluetoothManager.adapter.isEnabled) {
            Toast.makeText(applicationContext, "Please check your bluetooth connection.", Toast.LENGTH_LONG).show()
        } else {
            checkPermissions()
            val agent = localDatabase.retrieveAgentName()
            val location = localDatabase.retrieveLocation()
            val data = localDatabase.retrieveBetDetails(headerSerial)
            val list: ArrayList<BetDetailsModel> = data
            var bets = ""

            list.forEach {
                bets += "[L]${if (it.isRambolito == "0"){it.betNumber}else{it.betNumber+"-R"} }[C]${formatter.format(it.win.toDouble())}[R]${it.amount+".00"}\n"
            }

            val printer = EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 48f, 32)
            printer
                .printFormattedText("[C]<img>${PrinterTextParserImg.bitmapToHexadecimalString(printer, this.applicationContext.resources.getDrawableForDensity(
                    R.drawable.recieptlogo, DisplayMetrics.DENSITY_MEDIUM))}</img>\n" +
                        "[L]\n" +
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