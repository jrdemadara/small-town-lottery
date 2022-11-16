package com.slicksoftcoder.smalltownlottery.features.bet

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
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.slicksoftcoder.smalltownlottery.R
import com.slicksoftcoder.smalltownlottery.features.dashboard.DashboardActivity
import com.slicksoftcoder.smalltownlottery.server.LocalDatabase
import com.slicksoftcoder.smalltownlottery.util.DateUtil
import com.slicksoftcoder.smalltownlottery.util.NetworkChecker
import java.util.*
import kotlin.properties.Delegates

class BetActivity : AppCompatActivity() {
    private lateinit var localDatabase: LocalDatabase
    private lateinit var networkChecker: NetworkChecker
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bet)
        val formatter: NumberFormat = DecimalFormat("#,###")
        dateUtil = DateUtil()
        localDatabase = LocalDatabase(this)
        recyclerView = findViewById(R.id.recyclerViewBets)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = BetAdapter()
        recyclerView.adapter = adapter
        imageViewStatus = findViewById(R.id.imageViewStatus)
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
        checkNetworkConnection()
        selectDrawTime()
        textViewTime.setOnClickListener {
            selectDrawTime()
        }
        val headerSerial: UUID = UUID.randomUUID()
        buttonBetAdd.setOnClickListener {
            if ("NW" == editTextBetNumber.text.toString().uppercase(Locale.ROOT)){
                val amount: Double = editTextBetAmount.text.toString().toDouble()
                val serial: UUID = UUID.randomUUID()
                winAmount =  (amount/5) * 2500
                /* Save Bet */
                localDatabase.insertBetDetails(serial.toString(),headerSerial.toString(),editTextBetNumber.text.toString().uppercase(Locale.ROOT),editTextBetAmount.text.toString(), winAmount.toString(), "2")
                val list = localDatabase.retrieveBetDetails(headerSerial.toString())
                /* Retrieve Bet */
                adapter?.addItems(list)
                /* Update UI */
                totalAmount += amount
                textViewTotal.text =  formatter.format(totalAmount).toString()+".00"
                editTextBetNumber.setText("")
                editTextBetAmount.setText("")
                Toast.makeText(applicationContext, "Bet has been added.", Toast.LENGTH_SHORT).show()
            }else{
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
                                winAmount =  (amount/5) * 2750
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
                                winAmount = 0.0
                                isRambolito = 0
                                Toast.makeText(applicationContext, "Bet has been added.", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }else{
                                if (amount >= 30){
                                    winAmount =  (amount/30) * 2750
                                    editTextBetAmount.setTextColor(Color.parseColor("#000000"));
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
                                    winAmount = 0.0
                                    isRambolito = 0
                                    Toast.makeText(applicationContext, "Bet has been added.", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                }else{
                                    Toast.makeText(applicationContext, "Amount for rambolito should not be less than 30.", Toast.LENGTH_LONG).show()
                                    editTextBetAmount.setTextColor(Color.parseColor("#F15555"));
                                    dialog.dismiss()
                                }
                            }
                        }else{
                            Toast.makeText(applicationContext, "Please select type.", Toast.LENGTH_SHORT).show()
                        }

                    }
                }else{
                    Toast.makeText(applicationContext, "Please fill the required fields.", Toast.LENGTH_SHORT).show()
                }

                if (editTextBetNumber.text.equals("NW")){

                }
            }
        }
        buttonBetConfirm.setOnClickListener {
            if (totalAmount <= 0){
                Toast.makeText(applicationContext, "Please place a bet first.", Toast.LENGTH_SHORT).show()
            }else{
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Confirm")
                builder.setMessage("Do you wish to continue?")
                builder.setIcon(R.drawable.ic_round_check_circle_24)
                builder.setPositiveButton("Yes"){ dialogInterface, _ ->
                    val agent = localDatabase.retrieveAgentSerial()
                    val draw = localDatabase.retrieveDrawSerial(drawTime)
                    val transCode: String = "B"+dateUtil.dateFormat()+dateUtil.currentTime()
                    val transCodeDash = transCode.replace("-","")
                    val transCodeCol = transCodeDash.replace(":","")
                    localDatabase.insertBetHeader(
                        headerSerial.toString(),
                        agent,
                        dateUtil.dateFormat(),
                        draw,
                        transCodeCol,
                        totalAmount.toString()
                    )
                    localDatabase.confirmBetDetails(headerSerial.toString())
                    showSuccess()
                    dialogInterface.dismiss()
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
        var draw: String = ""
        if ("2 PM" == textViewTime.text){
            radioButton2pm.isChecked = true
            drawTime = "2 PM"
            draw = "2 PM"
        }else if("5 PM" == textViewTime.text){
            radioButton5pm.isChecked = true
            drawTime = "5 PM"
            draw = "5 PM"
        }else{
            radioButton9pm.isChecked = true
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
            if (draw.isNotEmpty()){
                textViewTime.text = draw
                dialog.dismiss()
            }else{
                Toast.makeText(applicationContext, "Please select draw time.", Toast.LENGTH_SHORT).show()
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

    private fun checkNetworkConnection() {
        networkChecker = NetworkChecker(application)
        networkChecker.observe(this) { isConnected ->
            if (isConnected) {
                imageViewStatus.setImageResource(R.drawable.online)
            } else {
                imageViewStatus.setImageResource(R.drawable.offline)
            }
        }
    }

}