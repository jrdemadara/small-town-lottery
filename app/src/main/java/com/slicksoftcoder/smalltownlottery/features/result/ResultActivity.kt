package com.slicksoftcoder.smalltownlottery.features.result

import android.app.Dialog // ktlint-disable import-ordering
// ktlint-disable import-ordering
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.ViewGroup
import android.widget.* // ktlint-disable no-wildcard-imports
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.muddassir.connection_checker.ConnectionState
import com.muddassir.connection_checker.checkConnection
import com.slicksoftcoder.smalltownlottery.R
import com.slicksoftcoder.smalltownlottery.common.model.ResultUpdateModel
import com.slicksoftcoder.smalltownlottery.features.dashboard.DashboardActivity
import com.slicksoftcoder.smalltownlottery.server.ApiInterface
import com.slicksoftcoder.smalltownlottery.server.LocalDatabase
import com.slicksoftcoder.smalltownlottery.server.NodeServer
import com.slicksoftcoder.smalltownlottery.util.DateUtil
import kotlinx.coroutines.* // ktlint-disable no-wildcard-imports
// ktlint-disable no-wildcard-imports
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ResultActivity : AppCompatActivity() {
    private lateinit var localDatabase: LocalDatabase
    private lateinit var dateUtil: DateUtil
    private lateinit var textViewResultDate: TextView
    private lateinit var textViewResult2pmResult: TextView
    private lateinit var textViewResult5pmResult: TextView
    private lateinit var textViewResult9pmResult: TextView
    private lateinit var floatingButtonResultBack: FloatingActionButton
    private lateinit var imageViewStatus: ImageView
    private lateinit var cardView2: CardView
    private lateinit var cardView5: CardView
    private lateinit var cardView9: CardView
    private var isConnected: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        dateUtil = DateUtil()
        localDatabase = LocalDatabase(this)
        textViewResultDate = findViewById(R.id.textViewResultDate)
        textViewResult2pmResult = findViewById(R.id.textViewResult2pmResult)
        textViewResult5pmResult = findViewById(R.id.textViewResult5pmResult)
        textViewResult9pmResult = findViewById(R.id.textViewResult9pmResult)
        floatingButtonResultBack = findViewById(R.id.floatingButtonResultBack)
        imageViewStatus = findViewById(R.id.imageViewResultStatus)
        cardView2 = findViewById(R.id.cardView2pmResult)
        cardView5 = findViewById(R.id.cardView5pmResult)
        cardView9 = findViewById(R.id.cardView9pmResult)
        textViewResultDate.text = dateUtil.currentDateShort().replace("-", " ").uppercase(Locale.ROOT)
        // * Check Internet Connection
        val connection = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connection.activeNetworkInfo

        if (activeNetwork != null) {
            isConnected = true
            imageViewStatus.setImageResource(R.drawable.online)
            updateResults()
        } else {
            imageViewStatus.setImageResource(R.drawable.offline)
            isConnected = false
        }
        checkConnection(this) { connectionState ->
            when (connectionState) {
                ConnectionState.CONNECTED -> {
                    isConnected = true
                    imageViewStatus.setImageResource(R.drawable.online)
                    updateResults()
                    retrieveResult()
                }
                ConnectionState.SLOW -> {
                    isConnected = true
                    imageViewStatus.setImageResource(R.drawable.slow)
                    updateResults()
                    retrieveResult()
                }
                else -> {
                    isConnected = false
                    imageViewStatus.setImageResource(R.drawable.offline)
                    retrieveResult()
                }
            }
        }

        result2pm()
        result5pm()
        result9pm()

        floatingButtonResultBack.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun retrieveResult() {
        val data = localDatabase.retrieveDrawResult(dateUtil.dateFormat())
        val list: ArrayList<ResultModel> = data
        if (list.size > 0) {
            list.forEach {
                textViewResult2pmResult.text = it.result2PM
                textViewResult5pmResult.text = it.result5PM
                textViewResult9pmResult.text = it.result9PM
            }
        }
    }

    private fun updateResults() {
        localDatabase.truncateResults()
        val retrofit = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
        retrofit.updateResults().enqueue(object : Callback<List<ResultUpdateModel>?> {
            override fun onResponse(
                call: Call<List<ResultUpdateModel>?>,
                response: Response<List<ResultUpdateModel>?>
            ) {
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
                    retrieveResult()
                }
            }

            override fun onFailure(call: Call<List<ResultUpdateModel>?>, t: Throwable) {
                resultStatus("No Result", "No new result has been release yet.", 0)
            }
        })
    }

    private fun result2pm() {
        cardView2.setOnClickListener {
            if (isConnected) {
                updateResults()
            } else {
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
                    localDatabase.deleteResult(drawSerial)
                    localDatabase.insertResult(serial.toString(), drawSerial, dateUtil.dateFormat(), editTextResult.text.toString(), dateUtil.dateFormat())
                    resultStatus("Success", "Result has been added.", 1)
                    retrieveResult()
                    dialog.dismiss()
                }
            }
        }
    }

    private fun result5pm() {
        cardView5.setOnClickListener {
            if (isConnected) {
                updateResults()
            } else {
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
                    localDatabase.deleteResult(drawSerial)
                    localDatabase.insertResult(serial.toString(), drawSerial, dateUtil.dateFormat(), editTextResult.text.toString(), dateUtil.dateFormat())
                    resultStatus("Sucesss", "Result has been added.", 1)
                    retrieveResult()
                    dialog.dismiss()
                }
            }
        }
    }

    private fun result9pm() {
        cardView9.setOnClickListener {
            if (isConnected) {
                updateResults()
            } else {
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
                    localDatabase.deleteResult(drawSerial)
                    localDatabase.insertResult(serial.toString(), drawSerial, dateUtil.dateFormat(), editTextResult.text.toString(), dateUtil.dateFormat())
                    resultStatus("Sucesss", "Result has been added.", 1)
                    retrieveResult()
                    dialog.dismiss()
                }
            }
        }
    }

    private fun resultStatus(title: String?, detail: String?, isSuccess: Int?) {
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
        if (isSuccess == 0) {
            imageView.setImageResource(R.drawable.exclamation)
        } else {
            imageView.setImageResource(R.drawable.ic_round_check_circle_24)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
        }, 2000)
    }
}
