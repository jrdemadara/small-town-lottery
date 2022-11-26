package com.slicksoftcoder.smalltownlottery.features.landing

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.khairo.escposprinter.EscPosPrinter
import com.khairo.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.khairo.escposprinter.textparser.PrinterTextParserImg
import com.slicksoftcoder.smalltownlottery.R
import com.slicksoftcoder.smalltownlottery.features.authenticate.AuthenticateActivity
import com.slicksoftcoder.smalltownlottery.features.bet.BetDetailsModel
import java.util.*

class LandingActivity : AppCompatActivity() {
    private lateinit var buttonGetStarted: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonGetStarted = findViewById(R.id.buttonGetStarted)
        buttonGetStarted.setOnClickListener {
            printReceipt()
            val intent = Intent(this, AuthenticateActivity::class.java)
            startActivity(intent)
            finish()
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

    private fun printReceipt(){
        val bluetoothManager = applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
        if (!bluetoothManager.adapter.isEnabled) {
            Toast.makeText(applicationContext, "Please check your bluetooth connection.", Toast.LENGTH_LONG).show()
        } else {
            checkPermissions()
            val printer = EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 48f, 32)
            printer
                .printFormattedText("[C]<img>${
                    PrinterTextParserImg.bitmapToHexadecimalString(printer, this.applicationContext.resources.getDrawableForDensity(
                        android.R.drawable.ic_media_play, DisplayMetrics.DENSITY_MEDIUM))}</img>\n" +
                        "[C]Printer is reader!\n".trimIndent()
                )
        }

    }
}