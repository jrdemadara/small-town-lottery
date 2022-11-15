package com.slicksoftcoder.smalltownlottery.features.bet

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.slicksoftcoder.smalltownlottery.R
import com.slicksoftcoder.smalltownlottery.server.LocalDatabase
import java.util.*
import kotlin.properties.Delegates

class BetActivity : AppCompatActivity() {
    private lateinit var localDatabase: LocalDatabase
    private lateinit var recyclerView: RecyclerView
    private var adapter: BetAdapter? = null
    private lateinit var textViewDate: TextView
    private lateinit var textViewTime: TextView
    private lateinit var textViewTotal: TextView
    private lateinit var editTextBetNumber: EditText
    private lateinit var editTextBetAmount: EditText
    private lateinit var buttonBetAdd: Button
    private lateinit var buttonBetConfirm: Button
    private var winAmount by Delegates.notNull<Double>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bet)
        localDatabase = LocalDatabase(this)
        recyclerView = findViewById(R.id.recyclerViewBets)
        adapter = BetAdapter()
        recyclerView.adapter = adapter
        textViewDate = findViewById(R.id.textViewBetDate)
        textViewTime = findViewById(R.id.textViewBetTime)
        textViewTotal = findViewById(R.id.textViewBetTotal)
        editTextBetNumber = findViewById(R.id.editTextBetNumber)
        editTextBetAmount = findViewById(R.id.editTextBetAmount)
        buttonBetAdd = findViewById(R.id.buttonBetAdd)
        buttonBetConfirm = findViewById(R.id.buttonBetConfirm)
        winAmount = 0.0
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
                var isRegular: Int = 1
                val amount: Double = editTextBetAmount.text.toString().toDouble()
                radioButtonRegular.setOnClickListener {
                    radioButtonRambolito.isChecked = false
                    isRegular = 1
                }
                radioButtonRambolito.setOnClickListener {
                    radioButtonRegular.isChecked = false
                    isRegular = 0
                }

                buttonConfirm.setOnClickListener {
                    if (isRegular == 1){
                        winAmount =  (amount/5) * 2750
                        dialog.dismiss()
                    }else{
                        if (amount >= 30){
                            winAmount =  (amount/30) * 2750
                            editTextBetAmount.setTextColor(Color.parseColor("#000000"));
                            dialog.dismiss()
                        }else{
                            Toast.makeText(applicationContext, "Amount for rambolito should not be less than 30.", Toast.LENGTH_LONG).show()
                            editTextBetAmount.setTextColor(Color.parseColor("#F15555"));
                            dialog.dismiss()
                        }
                    }
                    localDatabase.insertBetDetails(serial.toString(),headerSerial.toString(),editTextBetNumber.text.toString(),editTextBetAmount.text.toString(), winAmount.toString(), isRegular.toString())
                    val list = localDatabase.retrieveBetDetails(headerSerial.toString())
                    adapter?.addItems(list)
                }
            }else{
                Toast.makeText(applicationContext, "Please fill the required fields.", Toast.LENGTH_SHORT).show()
            }

            if (editTextBetNumber.text.equals("NW")){

            }
        }
    }

}