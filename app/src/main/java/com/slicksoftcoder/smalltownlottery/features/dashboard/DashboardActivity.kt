package com.slicksoftcoder.smalltownlottery.features.dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.slicksoftcoder.smalltownlottery.R
import com.slicksoftcoder.smalltownlottery.features.bet.BetActivity
import com.slicksoftcoder.smalltownlottery.server.LocalDatabase
import com.slicksoftcoder.smalltownlottery.util.DateUtil
import com.slicksoftcoder.smalltownlottery.util.NetworkChecker
import java.util.*

class DashboardActivity : AppCompatActivity() {
    private lateinit var networkChecker: NetworkChecker
    private lateinit var localDatabase: LocalDatabase
    private lateinit var dateUtil: DateUtil
    private lateinit var textViewDate: TextView
    private lateinit var textViewTotalBet: TextView
    private lateinit var textViewTotalHits: TextView
    private lateinit var textViewPNL: TextView
    private lateinit var textViewMenuBet: TextView
    private lateinit var textViewMenuHistory: TextView
    private lateinit var textViewMenuTransaction: TextView
    private lateinit var textViewMenu: TextView
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
    private lateinit var imageViewBet: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        dateUtil = DateUtil()
        textViewDate = findViewById(R.id.textViewDashDate)
        textViewTotalBet = findViewById(R.id.textViewDashTotalBet)
        textViewTotalHits = findViewById(R.id.textViewDashTotalHits)
        textViewPNL = findViewById(R.id.textViewDashPNL)
        textViewMenuBet = findViewById(R.id.textViewDashMenuBet)
        textViewMenuHistory = findViewById(R.id.textViewDashMenuHistory)
        textViewMenuTransaction = findViewById(R.id.textViewDashMenuTransaction)
        textViewMenu = findViewById(R.id.textViewDashMenu)
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
        textViewDate.text = dateUtil.currentDateShort().replace("-", " ").uppercase(Locale.ROOT)
        textViewMenuBet.setOnClickListener {
            val intent = Intent(this, BetActivity ::class.java)
            startActivity(intent)
            finish()
        }
        imageViewBet.setOnClickListener {
            val intent = Intent(this, BetActivity ::class.java)
            startActivity(intent)
            finish()
        }
    }

}