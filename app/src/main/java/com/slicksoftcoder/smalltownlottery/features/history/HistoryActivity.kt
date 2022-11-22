package com.slicksoftcoder.smalltownlottery.features.history

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.slicksoftcoder.smalltownlottery.R
import com.slicksoftcoder.smalltownlottery.features.dashboard.DashboardActivity
import com.slicksoftcoder.smalltownlottery.server.ApiInterface
import com.slicksoftcoder.smalltownlottery.server.LocalDatabase
import com.slicksoftcoder.smalltownlottery.server.NodeServer
import com.slicksoftcoder.smalltownlottery.util.DateUtil
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryActivity : AppCompatActivity() {
    private lateinit var localDatabase: LocalDatabase
    private lateinit var dateUtil: DateUtil
    private lateinit var recyclerViewHistory: RecyclerView
    private var adapterHistory: HistoryAdapter? = null
    private lateinit var floatingButtonHistoryBack: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        localDatabase = LocalDatabase(this)
        dateUtil = DateUtil()
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory)
        floatingButtonHistoryBack = findViewById(R.id.floatingButtonHistoryBack)
        recyclerViewHistory.layoutManager = LinearLayoutManager(this)
        adapterHistory = HistoryAdapter()
        recyclerViewHistory.adapter = adapterHistory
        retrieveHistory()
        floatingButtonHistoryBack.setOnClickListener {
            val intent = Intent(this, DashboardActivity ::class.java)
            startActivity(intent)
            finish()
        }

        adapterHistory!!.setOnClickItem {
            dialogBets(it.headerSerial,it.transactionCode,it.drawDate,it.drawTime,it.totalAmount,it.isVoid.toInt())
        }
    }

    private fun retrieveHistory(){
        val list = localDatabase.retrieveHistory()
        adapterHistory?.addItems(list)
    }

    private fun dialogBets(headerSerial: String, transaction: String, date: String, draw: String, amount:String ,status:Int) {
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
        if (0 == status){
            textViewHistoryBetStatus.text = "ACTIVE"
        }else{
            textViewHistoryBetStatus.text = "VOID"
        }
        buttonHistoryBetPrint.setOnClickListener {
            Toast.makeText(applicationContext, "Print", Toast.LENGTH_SHORT).show()
        }
        buttonHistoryBetVoid.setOnClickListener {
            if (status == 0){
                alertDialog(transaction, headerSerial,dialog,0)
            }else{
                alertDialog(transaction, headerSerial,dialog,1)
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
            Toast.makeText(applicationContext, "$transaction has been updated.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        builder.setNeutralButton("No"){ dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

}

