package com.slicksoftcoder.smalltownlottery.features.transmit

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.slicksoftcoder.smalltownlottery.R
import com.slicksoftcoder.smalltownlottery.common.model.BetDetailsTransmitModel
import com.slicksoftcoder.smalltownlottery.common.model.BetHeaderTransmitModel
import com.slicksoftcoder.smalltownlottery.common.model.DrawUpdateModel
import com.slicksoftcoder.smalltownlottery.common.model.ResultUpdateModel
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
import java.util.ArrayList
import kotlin.properties.Delegates

class TransmitActivity : AppCompatActivity() {
    private lateinit var localDatabase: LocalDatabase
    private lateinit var networkChecker: NetworkChecker
    private lateinit var imageView: ImageView
    private lateinit var imageViewStatus: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private lateinit var textViewWait: TextView
    private lateinit var textViewTagline: TextView
    private lateinit var buttonClose: Button
    private lateinit var dateUtil: DateUtil
    private var uploadProgress: Double = 0.0
    private var statusHeader by Delegates.notNull<Int>()
    private var statusDetails by Delegates.notNull<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transmit)
        localDatabase = LocalDatabase(this)
        imageView =findViewById(R.id.imageViewUpload)
        imageViewStatus =findViewById(R.id.imageViewTransmitStatus)
        progressBar = findViewById(R.id.progressBar)
        progressText = findViewById(R.id.textviewProgress)
        textViewWait = findViewById(R.id.textViewWait)
        textViewTagline = findViewById(R.id.textViewTagline)
        buttonClose = findViewById(R.id.buttonUploadClose)
        buttonClose.isVisible = false
        dateUtil = DateUtil()
        statusHeader = 0
        statusDetails = 0
        networkChecker = NetworkChecker(application)
        networkChecker.observe(this) { isConnected ->
            if (isConnected) {
                imageViewStatus.setImageResource(R.drawable.online)
            } else {
                imageViewStatus.setImageResource(R.drawable.offline)
            }
        }
        updateProgressBar()
        uploadBetHeader()
        uploadBetDetails()
        updateResults()

        buttonClose.setOnClickListener{
            if (statusHeader == 1 && statusDetails == 1){
                updateProgressBar()
                uploadBetHeader()
                uploadBetDetails()
            }else{
                val intent = Intent(this, DashboardActivity ::class.java)
                startActivity(intent)
                finish()
            }

            if (statusHeader == 0){
                val intent = Intent(this, DashboardActivity ::class.java)
                startActivity(intent)
                finish()
            }else{
                updateProgressBar()
                uploadBetHeader()
            }
            if (statusDetails == 0){
                val intent = Intent(this, DashboardActivity ::class.java)
                startActivity(intent)
                finish()
            }else{
                updateProgressBar()
                uploadBetDetails()
            }

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
                //Toast.makeText(applicationContext, "", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun uploadBetHeader(){
        val retrofit = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
        val data = localDatabase.transmitBetHeaders()
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
                    @SuppressLint("SetTextI18n")
                    override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                        if (response.code() == 200) {
                            localDatabase.updateBetHeaderTransmitted(it.serial, dateUtil.dateFormat() + " " + dateUtil.currentTimeComplete(), 1)
                        } else {
                            textViewTagline.text = "Something went wrong!"
                            textViewWait.text = "Please contact the system developer."
                            textViewTagline.setTextColor(ContextCompat.getColor(applicationContext, R.color.pink_500))
                            textViewWait.setTextColor(ContextCompat.getColor(applicationContext, R.color.pink_500))
                            buttonClose.isVisible = true
                            buttonClose.text = "Try Again"
                            statusHeader = 1
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        imageView.setImageResource(R.drawable.ic_bug_fixing)
                        progressText.text = "Error 500"
                        textViewTagline.setTextColor(ContextCompat.getColor(applicationContext, R.color.pink_500))
                        textViewTagline.text = "Something went wrong!"
                        textViewWait.text = "Please check your internet connection and try again."
                        buttonClose.isVisible = true
                        buttonClose.text = "Try Again"
                        statusHeader = 1
                    }

                })
            }
        }else{
            imageView.setImageResource(R.drawable.ic_empty)
            progressText.text = "Empty"
            textViewTagline.setTextColor(ContextCompat.getColor(applicationContext, R.color.pink_500))
            textViewTagline.text = "Oops!"
            textViewWait.text = "There is nothing to upload!"
            buttonClose.isVisible = true
            statusHeader = 0
        }

    }

    private fun uploadBetDetails(){
        val retrofit = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
        val data = localDatabase.transmitBetDetails()
        val list: ArrayList<BetDetailsTransmitModel> = data
        val size = list.size
        if (list.size > 0){
            val progressStep: Double = (100 / size).toDouble()
            list.forEach{
                val filter = HashMap<String, String>()
                filter["serial"] = it.serial
                filter["headerserial"] = it.headerSerial
                filter["betno"] = it.betNumber
                filter["totalamount"] = it.amount
                filter["win"] = it.win
                filter["isrambolito"] = it.isRambolito
                retrofit.transmitBetDetails(filter).enqueue(object : Callback<ResponseBody?> {
                    @SuppressLint("SetTextI18n")
                    override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                        if (response.code() == 200) {
                            localDatabase.updateBetDetailTransmitted(it.serial,dateUtil.dateFormat() + " " + dateUtil.currentTimeComplete(), 1)
                            if (uploadProgress <= 90) {
                                uploadProgress += progressStep
                                updateProgressBar()
                            }
                            if (uploadProgress > 89){
                                uploadProgress = 100.00
                                updateProgressBar()
                                buttonClose.isVisible = true
                                imageView.setImageResource(R.drawable.ic_happy)
                                textViewTagline.setTextColor(ContextCompat.getColor(applicationContext, R.color.yellow_500))
                                textViewTagline.text = "Upload completed!"
                                textViewWait.isVisible = false
                            }
                        } else {
                            textViewTagline.text = "Something went wrong!"
                            textViewWait.text = "Please contact the system developer."
                            textViewTagline.setTextColor(ContextCompat.getColor(applicationContext, R.color.pink_500))
                            textViewWait.setTextColor(ContextCompat.getColor(applicationContext, R.color.pink_500))
                            buttonClose.text = "Try Again"
                            statusDetails = 1
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        imageView.setImageResource(R.drawable.ic_bug_fixing)
                        progressText.text = "Error 500"
                        textViewTagline.setTextColor(ContextCompat.getColor(applicationContext, R.color.pink_500))
                        textViewTagline.text = "Something went wrong!"
                        textViewWait.text = "Please check your internet connection and try again."
                        buttonClose.isVisible = true
                        buttonClose.text = "Try Again"
                        statusDetails = 1
                    }
                })
            }
        }else{
            imageView.setImageResource(R.drawable.ic_empty)
            progressText.text = "Empty"
            textViewTagline.setTextColor(ContextCompat.getColor(applicationContext, R.color.pink_500))
            textViewTagline.text = "Oops!"
            textViewWait.text = "There is nothing to upload!"
            buttonClose.isVisible = true
            statusDetails = 0
        }

    }

    private fun updateProgressBar() {
        progressBar.progress = uploadProgress.toInt()
        progressText.text = "$uploadProgress%"
    }

    private fun switchActivity(){
        val intent = Intent(this, DashboardActivity ::class.java)
        startActivity(intent)
        finish()
    }

}