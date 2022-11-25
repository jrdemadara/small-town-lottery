package com.slicksoftcoder.smalltownlottery.features.result

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.slicksoftcoder.smalltownlottery.R
import com.slicksoftcoder.smalltownlottery.features.dashboard.DashboardActivity
import com.slicksoftcoder.smalltownlottery.server.LocalDatabase
import com.slicksoftcoder.smalltownlottery.util.DateUtil
import com.slicksoftcoder.smalltownlottery.util.NetworkChecker
import kotlinx.coroutines.*
import java.util.*

class ResultActivity : AppCompatActivity() {
    private lateinit var localDatabase: LocalDatabase
    private lateinit var networkChecker: NetworkChecker
    private lateinit var dateUtil: DateUtil
    private lateinit var textViewResultDate: TextView
    private lateinit var textViewResultTime: TextView
    private lateinit var textViewResult2pmResult: TextView
    private lateinit var textViewResult5pmResult: TextView
    private lateinit var textViewResult9pmResult: TextView
    private lateinit var floatingButtonResultBack: FloatingActionButton
    private lateinit var cardView2: CardView
    private lateinit var cardView5: CardView
    private lateinit var cardView9: CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        dateUtil = DateUtil()
        localDatabase = LocalDatabase(this)
        textViewResultDate = findViewById(R.id.textViewResultDate)
        textViewResultTime = findViewById(R.id.textViewResultTimeNow)
        textViewResult2pmResult = findViewById(R.id.textViewResult2pmResult)
        textViewResult5pmResult = findViewById(R.id.textViewResult5pmResult)
        textViewResult9pmResult = findViewById(R.id.textViewResult9pmResult)
        floatingButtonResultBack = findViewById(R.id.floatingButtonResultBack)
        cardView2 = findViewById(R.id.cardView2pmResult)
        cardView5 = findViewById(R.id.cardView5pmResult)
        cardView9 = findViewById(R.id.cardView9pmResult)
        textViewResultDate.text = dateUtil.currentDateShort().replace("-", " ").uppercase(Locale.ROOT)
        textViewResultTime.text = dateUtil.currentTime()
        lifecycleScope.launch(Dispatchers.IO){
            val drawSerial2pm =  localDatabase.retrieve2pmDrawSerial()
            val drawSerial5pm = localDatabase.retrieve5pmDrawSerial()
            val drawSerial9pm =  localDatabase.retrieve9pmDrawSerial()
            val result2pm = localDatabase.retrieve2pmDrawResult(drawSerial2pm)
            val result5pm =  localDatabase.retrieve5pmDrawResult(drawSerial5pm)
            val result9pm =  localDatabase.retrieve9pmDrawResult(drawSerial9pm)
            withContext(Dispatchers.Main){
                textViewResult2pmResult.text = result2pm
                textViewResult5pmResult.text = result5pm
                textViewResult9pmResult.text = result9pm
            }
        }

        result2pm()
        result5pm()
        result9pm()

        floatingButtonResultBack.setOnClickListener {
            val intent = Intent(this, DashboardActivity ::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun result2pm(){
        cardView2.setOnClickListener {
            val dialog = Dialog(this)
            val view = layoutInflater.inflate(R.layout.add_result_dialog, null)
            dialog.setCancelable(true)
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.setContentView(view)
            dialog.show()
            val textViewResultTime: TextView = view.findViewById(R.id.textViewResultTime)
            val editTextResult: EditText = view.findViewById(R.id.editTextResultNumber)
            val buttonResultConfirm: Button = view.findViewById(R.id.buttonResultConfirm)
            val serial: UUID = UUID.randomUUID()
            buttonResultConfirm.setOnClickListener {
                val drawSerial = localDatabase.retrieve2pmDrawSerial()
                textViewResultTime.text = "Add Result (\"2 PM\")"
                localDatabase.insertResult(serial.toString(), drawSerial, dateUtil.dateFormat(), editTextResult.text.toString(),dateUtil.dateFormat() + " " + dateUtil.currentTimeComplete())
                Toast.makeText(applicationContext, "Result has been saved.", Toast.LENGTH_SHORT).show()
                textViewResult2pmResult.text = localDatabase.retrieve2pmResult(drawSerial).toString()
                dialog.dismiss()
            }
        }
    }

    private fun result5pm(){
        cardView5.setOnClickListener {
            val dialog = Dialog(this)
            val view = layoutInflater.inflate(R.layout.add_result_dialog, null)
            dialog.setCancelable(true)
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.setContentView(view)
            dialog.show()
            val textViewResultTime: TextView = view.findViewById(R.id.textViewResultTime)
            val editTextResult: EditText = view.findViewById(R.id.editTextResultNumber)
            val buttonResultConfirm: Button = view.findViewById(R.id.buttonResultConfirm)
            val serial: UUID = UUID.randomUUID()
            buttonResultConfirm.setOnClickListener {
                val drawSerial = localDatabase.retrieve5pmDrawSerial()
                textViewResultTime.text = "Add Result (\"5 PM\")"
                localDatabase.insertResult(serial.toString(), drawSerial, dateUtil.dateFormat(), editTextResult.text.toString(),dateUtil.dateFormat() + " " + dateUtil.currentTimeComplete())
                Toast.makeText(applicationContext, "Result has been saved.", Toast.LENGTH_SHORT).show()
                textViewResult5pmResult.text = localDatabase.retrieve5pmResult(drawSerial).toString()
                dialog.dismiss()
            }
        }
    }

    private fun result9pm(){
        cardView9.setOnClickListener {
            val dialog = Dialog(this)
            val view = layoutInflater.inflate(R.layout.add_result_dialog, null)
            dialog.setCancelable(true)
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.setContentView(view)
            dialog.show()
            val textViewResultTime: TextView = view.findViewById(R.id.textViewResultTime)
            val editTextResult: EditText = view.findViewById(R.id.editTextResultNumber)
            val buttonResultConfirm: Button = view.findViewById(R.id.buttonResultConfirm)
            val serial: UUID = UUID.randomUUID()
            buttonResultConfirm.setOnClickListener {
                val drawSerial = localDatabase.retrieve9pmDrawSerial()
                textViewResultTime.text = "Add Result (\"9 PM\")"
                localDatabase.insertResult(serial.toString(), drawSerial, dateUtil.dateFormat(), editTextResult.text.toString(),dateUtil.dateFormat() + " " + dateUtil.currentTimeComplete())
                Toast.makeText(applicationContext, "Result has been saved.", Toast.LENGTH_SHORT).show()
                textViewResult9pmResult.text = localDatabase.retrieve9pmResult(drawSerial).toString()
                dialog.dismiss()
            }
        }
    }
}